package com.uli28.wireflowcreator.wireflows.idlingresources

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.IdlingResource

// https://stackoverflow.com/questions/50096441/how-to-make-espresso-wait-for-activity-that-is-triggered-by-a-firebase-call
class ActivityIdlingResource<T> constructor(
    private val mainActivity: T
) : IdlingResource where T : AppCompatActivity {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return (ActivityIdlingResource::class.java.name + System.currentTimeMillis())
    }

    override fun isIdleNow(): Boolean {
        return mainActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}