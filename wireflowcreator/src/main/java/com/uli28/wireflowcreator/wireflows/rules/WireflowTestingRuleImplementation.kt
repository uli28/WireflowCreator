package com.uli28.wireflowcreator.wireflows.rules

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import com.uli28.wireflowcreator.wireflows.config.BuildConfigValueProvider.Companion.isWireflowCreationEnabled
import com.uli28.wireflowcreator.wireflows.entities.FlowPresentation
import com.uli28.wireflowcreator.wireflows.entities.TestedRequirement
import com.uli28.wireflowcreator.wireflows.entities.Wireflow
import com.uli28.wireflowcreator.wireflows.extensions.ScreenshotRecorder
import com.uli28.wireflowcreator.wireflows.idlingresources.ActivityIdlingResource
import com.uli28.wireflowcreator.wireflows.idlingresources.ViewVisibilityIdlingResource
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WireflowTestingRuleImplementation<T>(
    private val activityRule: ActivityScenarioRule<T>,
    private val statement: Statement,
    private val description: Description,
    private val wireflowInitialisationRule: WireflowInitialisationRule,
    private val idlingResource: IdlingResource?,
    private val activityId: Int
) : Statement() where T : AppCompatActivity {
    private var activityIdlingResource: ActivityIdlingResource<T>? = null
    private var viewVisibilityIdlingResource: ViewVisibilityIdlingResource? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun evaluate() {
        if (!isWireflowCreationEnabled()) {
            statement.evaluate()
            return
        }
        idlingResource?.let {
            IdlingRegistry.getInstance().register(idlingResource)
        }

        val testedRequirements = description
            .annotations
            .filterIsInstance<CreateWireflow>()
            .firstOrNull()
            ?.requirements

        wireflowInitialisationRule.flowPresentation?.flows =
            addFlowWithRequirements(
                description,
                testedRequirements,
                wireflowInitialisationRule.flowPresentation
            )
        // Do something before test.
        val startTime = System.currentTimeMillis()
        try {
            // Execute the test.
            statement.evaluate()
        } finally {
            // Do something after the test.
            val endTime = System.currentTimeMillis()
            println("${description.methodName} took ${endTime - startTime} ms)")
            idlingResource?.let {
                IdlingRegistry.getInstance().unregister(idlingResource)
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

    private fun addInitialScreenshot(targetFlow: Wireflow) {
        var steps = targetFlow.steps
        if (steps == null) {
            steps = mutableListOf()
        }

        val currentActivity = registerIdlingResourcesForView()
        Espresso.onIdle() // https://stackoverflow.com/questions/33120493/espresso-idling-resource-doesnt-work

        steps.add(ScreenshotRecorder(currentActivity).createScreenshot())
        unregisterIdlingResourcesForView()
        targetFlow.steps = steps
    }

    private fun unregisterIdlingResourcesForView() {
        IdlingRegistry.getInstance()
            .unregister(activityIdlingResource)
        IdlingRegistry.getInstance()
            .unregister(viewVisibilityIdlingResource)
    }

    private fun registerIdlingResourcesForView(): Activity? {
        var view: View? = null
        var currentActivity: Activity? = null
        activityRule.scenario.onActivity { activity ->
            currentActivity = activity
            activityIdlingResource = ActivityIdlingResource(activity)
            IdlingRegistry.getInstance()
                .register(activityIdlingResource)
            view = activity.findViewById(activityId)
        }
        view?.let {
            viewVisibilityIdlingResource =
                ViewVisibilityIdlingResource(
                    view!!,
                    VISIBLE
                )
            IdlingRegistry.getInstance()
                .register(viewVisibilityIdlingResource)
        }
        return currentActivity
    }

    private fun addTestedRequirements(createdWireflow: Wireflow, requirements: Array<Requirement>?) {
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