package com.uli28.wireflowcreator

import com.codingwithmitch.espressouitestexamples.ui.main.wireflows.rules.WireflowInitialisationRule
import com.codingwithmitch.espressouitestexamples.ui.main.wireflows.rules.WireflowTestingRule
import org.junit.ClassRule
import org.junit.Rule

open class WireflowCreator {
    companion object {
        @get:ClassRule
        @JvmStatic
        val wireflowInitialisationRule = WireflowInitialisationRule()
    }

    @get:Rule
    val wireflowTestingRule = WireflowTestingRule(wireflowInitialisationRule)
}