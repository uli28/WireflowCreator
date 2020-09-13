package com.uli28

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.uli28.app.MainActivity
import com.uli28.wireflowcreator.app.R
import com.uli28.wireflowcreator.wireflows.WireflowCreator
import com.uli28.wireflowcreator.wireflows.annotations.CreateFlowRepresentation
import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import com.uli28.wireflowcreator.wireflows.rules.WireflowInitialisationRule
import com.uli28.wireflowcreator.wireflows.rules.WireflowTestingRule
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@CreateFlowRepresentation(name = "myWireflowName")
class MainActivityTest : WireflowCreator(wireflowInitialisationRule) {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    companion object {
        @get:ClassRule
        @JvmStatic
        val wireflowInitialisationRule = WireflowInitialisationRule(getApplicationContext())
    }

    @Test
    @CreateWireflow([Requirement(id = "MPO-123", link = "https://www.google.at")])
    fun test_navSecondaryActivity() {
        wireflowTestingRule.onView(withId(R.id.button_next_activity)).perform(click())

        wireflowTestingRule.onView(withId(R.id.secondary)).check(matches(isDisplayed()))
    }

    /**
     * Test both ways to navigate from SecondaryActivity to MainActivity
     */
    @Test
    fun test_backPress_toMainActivity() {

        wireflowTestingRule.onView(withId(R.id.button_next_activity)).perform(click())

        wireflowTestingRule.onView(withId(R.id.secondary)).check(matches(isDisplayed()))

        wireflowTestingRule.onView(withId(R.id.button_back)).perform(click())

        println("done")
    }
}










