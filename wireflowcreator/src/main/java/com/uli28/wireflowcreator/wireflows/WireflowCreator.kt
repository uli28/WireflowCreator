package com.uli28.wireflowcreator.wireflows

import com.uli28.wireflowcreator.wireflows.rules.WireflowInitialisationRule
import com.uli28.wireflowcreator.wireflows.rules.WireflowTestingRule
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