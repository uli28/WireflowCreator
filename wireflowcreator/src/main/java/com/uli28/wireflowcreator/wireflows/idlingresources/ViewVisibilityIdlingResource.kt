package com.uli28.wireflowcreator.wireflows.idlingresources

import android.os.Handler
import android.view.View
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import java.lang.ref.WeakReference


/**
 * [IdlingResource] which monitors a [View] for a given visibility state. The resource is considered idle when the
 * View has the desired state.
 *
 * url: https://gist.github.com/vaughandroid/e2fda716c7cf6853fa79
 * @author vaughandroid@gmail.com
 */
class ViewVisibilityIdlingResource(view: View, visibility: Int) :
    IdlingResource {
    private val mView: WeakReference<View> = WeakReference(view)
    private val mVisibility: Int = visibility
    private var mResourceCallback: ResourceCallback? = null

    override fun getName(): String {
        return (ViewVisibilityIdlingResource::class.java.name + System.currentTimeMillis())
    }

    override fun isIdleNow(): Boolean {
        val view = mView.get()
        val isIdle = view == null || view.visibility == mVisibility
        if (isIdle) {
            if (mResourceCallback != null) {
                mResourceCallback!!.onTransitionToIdle()
            }
        } else {
            /* Force a re-check of the idle state in a little while.
             * If isIdleNow() returns false, Espresso only polls it every few seconds which can slow down our tests.
             * Ideally we would watch for the visibility state changing, but AFAIK we can't detect when a View's
             * visibility changes to GONE.
             */
            Handler().postDelayed({ isIdleNow }, IDLE_POLL_DELAY_MILLIS.toLong())
        }
        return isIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        mResourceCallback = resourceCallback
    }

    companion object {
        private const val IDLE_POLL_DELAY_MILLIS = 100
    }
}