package com.uli28

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.uli28.app.EspressoIdlingResource
import com.uli28.app.MainActivity
import com.uli28.wireflowcreator.app.R
import com.uli28.wireflowcreator.wireflows.annotations.CreateFlowRepresentation
import com.uli28.wireflowcreator.wireflows.annotations.CreateWireflow
import com.uli28.wireflowcreator.wireflows.annotations.Requirement
import com.uli28.wireflowcreator.wireflows.rules.WireflowInitialisationRule
import com.uli28.wireflowcreator.wireflows.rules.WireflowTestingRule
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

class LoginIdlingResource<T> constructor(
    private val mainActivity: MainActivity
) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return LoginIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {
        return !mainActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) // <----- Important part
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}

@RunWith(AndroidJUnit4ClassRunner::class)
@CreateFlowRepresentation(name = "myWireflowName")
class MainActivityTest {

    companion object {
        @get:ClassRule
        @JvmStatic
        val wireflowInitialisationRule = WireflowInitialisationRule(getApplicationContext())
    }

    private val activityRule = activityScenarioRule<MainActivity>()


    private val wireflowTestingRule = WireflowTestingRule(activityRule, wireflowInitialisationRule, EspressoIdlingResource.countingIdlingResource)


    @get:Rule
    val chain: RuleChain = RuleChain
        .outerRule(activityRule)
        .around(wireflowTestingRule);

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
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
    @CreateWireflow([Requirement(id = "MPO-123", link = "https://www.google.at")])
    fun test_backPress_toMainActivity() {

        wireflowTestingRule.onView(withId(R.id.button_next_activity)).perform(click())

        wireflowTestingRule.onView(withId(R.id.secondary)).check(matches(isDisplayed()))

        wireflowTestingRule.onView(withId(R.id.button_back)).perform(click())

        println("done")
    }
}










