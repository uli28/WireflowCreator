package com.uli28

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.uli28.app.MainActivity
import com.uli28.wireflowcreator.app.R
import com.uli28.wireflowcreator.wireflows.WireflowCreator
import com.uli28.wireflowcreator.wireflows.annotations.CreateFlowPresentation
import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@CreateFlowPresentation(name = "myFlow")
class MainActivityTest: WireflowCreator() {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    @CreateWireflow([Requirement(id = "MPO-123", link = "https://www.google.at")])
    fun test_navSecondaryActivity() {
        Espresso.onView(withId(R.id.button_next_activity)).perform(click())

        Espresso.onView(withId(R.id.secondary)).check(matches(isDisplayed()))
    }

    /**
     * Test both ways to navigate from SecondaryActivity to MainActivity
     */
    @Test
    fun test_backPress_toMainActivity() {

        Espresso.onView(withId(R.id.button_next_activity)).perform(click())

        Espresso.onView(withId(R.id.secondary)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.button_back)).perform(click()) // method 1

//        wireflowTestingRule.onView(withId(R.id.main)).check(matches(isDisplayed()))
//
//        wireflowTestingRule.onView(withId(R.id.button_next_activity)).perform(click())
//
//        pressBack() // method 2
//
//        onView(withId(R.id.main)).check(matches(isDisplayed()))

        println("done")
    }
}










