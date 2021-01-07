package com.uli28.wireflowcreator.wireflows.rules

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import com.uli28.wireflowcreator.wireflows.config.BuildConfigValueProvider.Companion.isWireflowCreationEnabled
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import com.uli28.wireflowcreator.wireflows.entities.TestStatus
import com.uli28.wireflowcreator.wireflows.entities.TestedRequirement
import com.uli28.wireflowcreator.wireflows.entities.Wireflow
import com.uli28.wireflowcreator.wireflows.extensions.ScreenshotRecorder
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WireflowTestingRuleImplementation(
    private val statement: Statement,
    private val description: Description,
    private val wireflowInitialisationRule: WireflowInitialisationRule,
    private val idlingResource: IdlingResource?
) : Statement() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun evaluate() {
        // Do something before test.
        val startTime = System.currentTimeMillis()
        idlingResource?.let {
            IdlingRegistry.getInstance().register(idlingResource)
        }

        if (!isWireflowCreationEnabled()) {
            try {
                statement.evaluate()
            } finally {
                // Do something after the test.
                val endTime = System.currentTimeMillis()
                println("${description.methodName} took ${endTime - startTime} ms")
                idlingResource?.let {
                    IdlingRegistry.getInstance().unregister(idlingResource)
                }
            }
            return
        }

        val testedRequirements = description
            .annotations
            .filterIsInstance<CreateWireflow>()
            .firstOrNull()
            ?.requirements

        val currentMillis = System.currentTimeMillis()

        wireflowInitialisationRule.flowPresentation?.flows =
            addFlowWithRequirements(
                description,
                testedRequirements,
                wireflowInitialisationRule.flowPresentation,
                currentMillis
            )
        var thrownException: Exception? = null
        try {
            // Execute the test.
            statement.evaluate()
        } catch (e: Exception) {
            // handler
            thrownException = e
            throw e
        } finally {
            // Do something after the test.
            val endTime = System.currentTimeMillis()
            val passedMilliSeconds = endTime - startTime
            println("${description.methodName} took $passedMilliSeconds ms")
            val passedSeconds = passedMilliSeconds / 1000.0
            val currentFlow =
                wireflowInitialisationRule.flowPresentation?.flows!![description.methodName + "_" + currentMillis]
            if (thrownException != null) {
                currentFlow?.testStatus =
                    thrownException.message?.let { TestStatus(passedSeconds, it) }
            } else {
                currentFlow?.testStatus = TestStatus(passedSeconds, null)
            }
            wireflowInitialisationRule.flowPresentation?.flows!![description.methodName + "_" + currentMillis] =
                currentFlow!!
            idlingResource?.let {
                IdlingRegistry.getInstance().unregister(idlingResource)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addFlowWithRequirements(
        description: Description,
        requirements: Array<Requirement>?,
        flowPresentation: FlowPresentation?,
        currentMillis: Long
    ): MutableMap<String, Wireflow> {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val currentTimeStamp = LocalDateTime.now().format(formatter)

        var flows = flowPresentation?.flows

        if (flows == null) {
            flows = mutableMapOf()
        }

        val createdWireflow = Wireflow(currentTimeStamp, description.methodName)

        addTestedRequirements(createdWireflow, requirements)
        addInitialScreenshot(createdWireflow)

        flows[description.methodName + "_" + currentMillis] = createdWireflow
        return flows
    }

    private fun addInitialScreenshot(targetFlow: Wireflow) {
        var steps = targetFlow.steps
        if (steps == null) {
            steps = mutableListOf()
        }

        Espresso.onIdle() // https://stackoverflow.com/questions/33120493/espresso-idling-resource-doesnt-work

        steps.add(ScreenshotRecorder(true).createScreenshot())
        targetFlow.steps = steps
    }

    private fun addTestedRequirements(
        createdWireflow: Wireflow,
        requirements: Array<Requirement>?
    ) {
        if (createdWireflow.testedRequirements == null) {
            createdWireflow.testedRequirements = mutableListOf()
        }

        requirements?.let {
            for (testedRequirement in requirements) {
                createdWireflow.testedRequirements?.add(
                    TestedRequirement(testedRequirement.id, testedRequirement.link)
                )
            }
        }
    }
}