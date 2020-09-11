package com.uli28.wireflowcreator.wireflows.rules


import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import com.uli28.wireflowcreator.wireflows.entities.TestedRequirement
import com.uli28.wireflowcreator.wireflows.entities.Wireflow
import com.uli28.wireflowcreator.wireflows.extensions.ScreenshotRecorder
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.lang.Thread.sleep

class WireflowTestingRuleImplementation(
    private val statement: Statement,
    private val description: Description,
    private val flowPresentation: FlowPresentation?
) : Statement() {
    override fun evaluate() {
        val testedRequirements = description
            .annotations
            .filterIsInstance<CreateWireflow>()
            .firstOrNull()
            ?.requirements
        sleep(50)
        flowPresentation?.flows =
            addFlowWithRequirements(description, testedRequirements, flowPresentation)
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

private fun addFlowWithRequirements(
    description: Description,
    requirements: Array<Requirement>?,
    flowPresentation: FlowPresentation?
): MutableMap<String, Wireflow> {
    var flows = flowPresentation?.flows

    if (flows == null) {
        flows = mutableMapOf()
    }

    val createdWireflow = Wireflow()

    addTestedRequirements(createdWireflow, requirements)
    addInitialScreenshot(createdWireflow)

    flows[description.methodName + "_" + System.currentTimeMillis()] = createdWireflow
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
