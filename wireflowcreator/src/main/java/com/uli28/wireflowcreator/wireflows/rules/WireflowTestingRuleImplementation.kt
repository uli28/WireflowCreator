package com.uli28.wireflowcreator.wireflows.rules


import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import com.uli28.wireflowcreator.wireflows.entities.TestedRequirement
import com.uli28.wireflowcreator.wireflows.entities.Wireflow
import com.uli28.wireflowcreator.wireflows.extensions.ScreenshotRecorder
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WireflowTestingRuleImplementation<T>(
    private val activityRule: ActivityScenarioRule<T>,
    private val statement: Statement,
    private val description: Description,
    private val wireflowInitialisationRule: WireflowInitialisationRule
) : Statement() where T : Activity {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun evaluate() {
        val testedRequirements = description
            .annotations
            .filterIsInstance<CreateWireflow>()
            .firstOrNull()
            ?.requirements

        while(!activityRule.scenario.state.isAtLeast(Lifecycle.State.RESUMED)) {
            sleep(100)
        }
        sleep(200)

        wireflowInitialisationRule.flowPresentation?.flows =
            addFlowWithRequirements(description, testedRequirements, wireflowInitialisationRule.flowPresentation)
        // Do something before test.
        val startTime = System.currentTimeMillis()
        try {
            // Execute the test.
            statement.evaluate()
        } finally {
            // Do something after the test.
            val endTime = System.currentTimeMillis()
            println("${description.methodName} took ${endTime - startTime} ms)")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun addFlowWithRequirements(
    description: Description,
    requirements: Array<Requirement>?,
    flowPresentation: FlowPresentation?
): MutableMap<String, Wireflow> {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val currentTimeStamp = LocalDateTime.now().format(formatter)
    val currentMillis = System.currentTimeMillis()

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

fun addInitialScreenshot(targetFlow: Wireflow) {
    var steps = targetFlow.steps
    if (steps == null) {
        steps = mutableListOf()
    }
    steps.add(ScreenshotRecorder().createScreenshot())
    targetFlow.steps = steps
}

fun addTestedRequirements(createdWireflow: Wireflow, requirements: Array<Requirement>?) {
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
