package com.uli28.wireflowcreator.extensions

import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import com.uli28.wireflowcreator.rules.WireflowInitialisationRule
import com.uli28.wireflowcreator.wireflows.entities.*
import org.junit.runner.Description

class RecordedViewInteraction(
    var wireflowInitialisationRule: WireflowInitialisationRule,
    var description: Description?,
    var viewInteraction: ViewInteraction
) {
    fun perform(vararg viewActions: ViewAction): RecordedViewInteraction {

        viewInteraction.check { view, _ ->
            recordPositions(view, wireflowInitialisationRule.flowPresentation, description)
        }

        val resultingViewInteraction = viewInteraction.perform(*viewActions)
        recordImage(wireflowInitialisationRule.flowPresentation, description)
        return RecordedViewInteraction(
            wireflowInitialisationRule,
            description,
            resultingViewInteraction
        )
    }

    fun check(viewAssertion: ViewAssertion): RecordedViewInteraction {
        val resultingViewInteraction = viewInteraction.check(viewAssertion)
        return RecordedViewInteraction(
            wireflowInitialisationRule,
            description,
            resultingViewInteraction
        )
    }

    private fun recordImage(
        flowPresentation: FlowPresentation?,
        description: Description?
    ) {
        val flows = flowPresentation?.flows
        flows?.let {
            val filteredFlow = flows.filterKeys { it.contains(description?.methodName.toString()) }
            val updatedTargetFlow = updateTargetFlow(filteredFlow)
            updatedTargetFlow?.let {
                flows[updatedTargetFlow.first] = updatedTargetFlow.second
                flowPresentation.flows = flows
            }
        }
    }

    private fun updateTargetFlow(
        filteredFlow: Map<String, Wireflow>
    ): Pair<String, Wireflow>? {
        if (filteredFlow.size != 1) {
            println("to many flows found!")
            return null
        }

        for ((key, targetFlow) in filteredFlow) {
            var steps = targetFlow.steps
            if (steps == null) {
                steps = mutableListOf()
            }
            steps.add(createStep())
            targetFlow.steps = steps
            return Pair(key, targetFlow)
        }

        return null
    }

    private fun createStep(): ImageType {
        return ScreenshotRecorder().createScreenshot()
    }


    private fun recordPositions(
        view: View?,
        flowPresentation: FlowPresentation?,
        description: Description?
    ) {
        val coordinates = IntArray(2)
        view?.getLocationOnScreen(coordinates)
        val x = coordinates[0]
        val y = coordinates[1]
        val width = view?.width
        val height = view?.height

        val flows = flowPresentation?.flows
        flows?.let {
            val filteredFlow = flows.filterKeys { it.contains(description?.methodName.toString()) }
            val updatedTargetFlow = updateTargetFlow(filteredFlow, x, y, width, height)
            updatedTargetFlow?.let {
                flows[updatedTargetFlow.first] = updatedTargetFlow.second
                flowPresentation.flows = flows
            }
        }
    }

    private fun updateTargetFlow(
        filteredFlow: Map<String, Wireflow>,
        x: Int,
        y: Int,
        width: Int?,
        height: Int?
    ): Pair<String, Wireflow>? {
        if (filteredFlow.size != 1) {
            println("to many flows found!")
            return null
        }

        for ((key, targetFlow) in filteredFlow) {
            var steps = targetFlow.steps
            if (steps == null) {
                steps = mutableListOf()
            }
            steps.add(createStep(x, y, width, height))
            targetFlow.steps = steps
            return Pair(key, targetFlow)
        }

        return null
    }

    private fun createStep(x: Int, y: Int, width: Int?, height: Int?): TransitionEventType {
        val transitionEventType = TransitionEventType()
        val coordinates = Coordinates()
        coordinates.x = x
        coordinates.y = y
        coordinates.width = width
        coordinates.height = height
        transitionEventType.coordinates = coordinates
        return transitionEventType
    }
}