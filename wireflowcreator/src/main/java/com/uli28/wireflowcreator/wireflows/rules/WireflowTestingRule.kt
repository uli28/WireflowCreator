package com.uli28.wireflowcreator.wireflows.rules

import android.view.View
import androidx.test.espresso.IdlingResource
import com.uli28.wireflowcreator.wireflows.extensions.RecordedViewInteraction
import com.uli28.wireflowcreator.wireflows.extensions.WireflowRecorder
import org.hamcrest.Matcher
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class WireflowTestingRule(
    private var wireflowInitialisationRule: WireflowInitialisationRule,
    private val idlingResource: IdlingResource?
) : TestRule {
    private var description: Description? = null
    override fun apply(base: Statement, description: Description) =
        createWireflowTestingRuleImplementation(base, description)

    private fun createWireflowTestingRuleImplementation(base: Statement, description: Description): Statement? {
        this.description = description
        return WireflowTestingRuleImplementation(base, description, wireflowInitialisationRule, idlingResource)
    }

    fun onView(viewMatcher: Matcher<View>): RecordedViewInteraction =
        WireflowRecorder().recordedOnView(viewMatcher, description, wireflowInitialisationRule)
}