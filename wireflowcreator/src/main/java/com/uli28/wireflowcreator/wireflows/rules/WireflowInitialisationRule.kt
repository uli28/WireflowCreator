package com.uli28.wireflowcreator.wireflows.rules

import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.gson.GsonBuilder
import com.uli28.wireflowcreator.wireflows.annotations.CreateFlowPresentation
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.time.LocalDate

class WireflowInitialisationRule : TestRule {
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
                .filterIsInstance<CreateFlowPresentation>()
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
        flowPresentation = FlowPresentation(name, LocalDate.now().toString(), "myApp")
        println(flowPresentation)
    }

    ///storage/emulated/0/Android/data/com.codingwithmitch.espressouitestexamples/files/Pictures
    private fun writeToJson() {
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val json: String = gsonPretty.toJson(flowPresentation)

        println(json)
        val context = getInstrumentation().targetContext.applicationContext

        File(context.getExternalFilesDir(DIRECTORY_PICTURES).toString() + File.separator + "xy.json")
            .writeText(json)
    }
}