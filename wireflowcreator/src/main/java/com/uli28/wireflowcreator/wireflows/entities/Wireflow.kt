package com.uli28.wireflowcreator.wireflows.entities

class Wireflow(var currentTimestamp: String, var testCaseName: String) {
    var testedRequirements: MutableList<TestedRequirement>? = null
    var steps: MutableList<Any>? = null
}