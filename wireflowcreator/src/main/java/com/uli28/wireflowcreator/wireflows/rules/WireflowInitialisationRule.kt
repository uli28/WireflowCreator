package com.uli28.wireflowcreator.wireflows.rules

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.uli28.wireflowcreator.BuildConfig
import com.uli28.wireflowcreator.wireflows.annotations.CreateFlowRepresentation
import com.uli28.wireflowcreator.wireflows.config.BuildConfigValueProvider.Companion.getBuildConfigValue
import com.uli28.wireflowcreator.wireflows.config.BuildConfigValueProvider.Companion.isWireflowCreationEnabled
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter.Companion.APPLICATION_ID
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter.Companion.BUILD_TIMESTAMP
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter.Companion.BUILD_TYPE
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter.Companion.DEFAULT_DATETIME_FORMAT
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter.Companion.FLAVOR
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch


class WireflowInitialisationRule(var context: Context?, var suffix: String?) : TestRule {
    var flowPresentation: FlowPresentation? = null

    override fun apply(base: Statement, description: Description) =
        WireflowInitialisationRule(base, description)

    inner class WireflowInitialisationRule(
        private val statement: Statement,
        private val description: Description
    ) : Statement() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun evaluate() {
            if (!isWireflowCreationEnabled()) {
                statement.evaluate()
                return
            }
            val name = description
                .annotations
                .filterIsInstance<CreateFlowRepresentation>()
                .firstOrNull()
                ?.name ?: ""

            // Do something before all tests.
            initWireflow(name, description)
            try {
                // Execute the test.
                statement.evaluate()
            } finally {
                // Do something after the test.
                writeToJson()
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun initWireflow(name: String, description: Description) {
            val buildTimestamp = getBuildConfigValue(context?.packageName + (suffix ?: ""), BUILD_TIMESTAMP).toString()
            val flavor = getBuildConfigValue(context?.packageName + suffix, FLAVOR).toString()
            val buildType = getBuildConfigValue(context?.packageName + suffix, BUILD_TYPE).toString()

            flowPresentation = FlowPresentation(
                name,
                buildTimestamp,
                getApplicationName(flavor, buildType),
                BuildConfig.VERSION_NAME,
                description.displayName.substringAfterLast(".")
            )
            println(flowPresentation)
        }

        private fun getApplicationName(flavor: String, buildType: String): String {
            if (flavor.isEmpty() || buildType.isEmpty()) {
                return getBuildConfigValue(context?.packageName + suffix, APPLICATION_ID)
                    .toString().substringAfterLast(".")
            }
            return flavor + buildType
        }

        ///storage/emulated/0/Android/data/com.codingwithmitch.espressouitestexamples/files/Pictures
        @RequiresApi(Build.VERSION_CODES.O)
        private fun writeToJson() {
            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
            val json: String = gsonPretty.toJson(flowPresentation)
            println(json)
            val instance = FirebaseDatabase.getInstance()
            val database = instance.reference

            val mAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser? = mAuth.currentUser
            if (user != null) {
                // do your stuff
            } else {
                signInAnonymously(mAuth)
            }
            val done = CountDownLatch(1)
            val formatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT)
            val buildDate = LocalDateTime.parse(flowPresentation?.buildDate, formatter)
            val idFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
            flowPresentation?.application?.let {
                database.child(it).child(buildDate.format(idFormatter))
                    .setValue(flowPresentation) { error, ref ->
                    println("worked")
                    done.countDown()
                    //setValue operation is done, you'll get null in errror and ref is the path reference for firebase database
                }
            }
            try {
                done.await() //it will wait till the response is received from firebase.
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

//        val context = getInstrumentation().targetContext.applicationContext

//        File(
//            context.getExternalFilesDir(DIRECTORY_PICTURES).toString() + File.separator + "xy.json"
//        )
//            .writeText(json)
        }

        private fun signInAnonymously(mAuth: FirebaseAuth) {
            mAuth.signInAnonymously()
                .addOnSuccessListener {
                    println("worked")
                }
                .addOnFailureListener {
                    // Handle unsuccessful uploads
                    println("didn't work")
                }
        }
    }
}