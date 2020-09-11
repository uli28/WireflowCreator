package com.uli28.wireflowcreator.wireflows.extensions

import android.view.View
import androidx.test.espresso.Espresso
import com.uli28.wireflowcreator.wireflows.rules.WireflowInitialisationRule
import org.hamcrest.Matcher
import org.junit.runner.Description

class WireflowRecorder {

    fun recordedOnView(
        viewMatcher: Matcher<View>,
        description: Description?,
        wireflowInitialisationRule: WireflowInitialisationRule
    ): RecordedViewInteraction {
        val viewInteraction = Espresso.onView(viewMatcher)
        return RecordedViewInteraction(wireflowInitialisationRule, description, viewInteraction)
    }
}