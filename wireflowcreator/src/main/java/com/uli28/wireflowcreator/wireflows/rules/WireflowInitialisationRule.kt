package com.uli28.wireflowcreator.wireflows.rules

import android.content.Context
import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.gson.GsonBuilder
import com.uli28.wireflowcreator.BuildConfig
import com.uli28.wireflowcreator.wireflows.annotations.CreateFlowRepresentation
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.lang.reflect.Field
import java.time.LocalDate

class WireflowInitialisationRule(var context: Context?) : TestRule {
    var flowPresentation: FlowPresentation? = null

    override fun apply(base: Statement, description: Description) =
        WireflowInitialisationRule(base, description)

    inner class WireflowInitialisationRule(
        private val statement: Statement,
        private val description: Description
    ) : Statement() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun evaluate() {
            val name = description
                .annotations
                .filterIsInstance<CreateFlowRepresentation>()
                .firstOrNull()
                ?.name ?: description.methodName + LocalDate.now().toString();

            // Do something before all tests.
            initWireflow(name)
            try {
                // Execute the test.
                statement.evaluate()
            } finally {
                // Do something after the test.
                writeToJson()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initWireflow(name: String) {
        val buildTimestamp = getBuildConfigValue(context!!, "BUILD_TIMESTAMP").toString()
        val flavor = getBuildConfigValue(context!!, "FLAVOR").toString()
        val buildType = getBuildConfigValue(context!!, "BUILD_TYPE").toString()

        flowPresentation = FlowPresentation(
            name,
            buildTimestamp,
            getApplicationName(flavor, buildType),
            BuildConfig.VERSION_NAME
        )
        println(flowPresentation)
    }

    private fun getApplicationName(flavor: String, buildType: String): String {
        if (flavor.isEmpty() || buildType.isEmpty()) {
            return getBuildConfigValue(context!!, "ULI_TEST").toString()
        }
        return flavor + buildType
    }

    ///storage/emulated/0/Android/data/com.codingwithmitch.espressouitestexamples/files/Pictures
    private fun writeToJson() {
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val json: String = gsonPretty.toJson(flowPresentation)

        println(json)
        val context = getInstrumentation().targetContext.applicationContext

        File(
            context.getExternalFilesDir(DIRECTORY_PICTURES).toString() + File.separator + "xy.json"
        )
            .writeText(json)
    }

    fun getBuildConfigValue(
        context: Context,
        fieldName: String?
    ): Any? {
        try {
            val clazz =
                Class.forName(context.packageName + ".BuildConfig")
            val field: Field = clazz.getField(fieldName!!)
            return field.get(null)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }
}