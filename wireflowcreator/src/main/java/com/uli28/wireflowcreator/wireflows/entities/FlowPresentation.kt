package com.uli28.wireflowcreator.wireflows.entities

class FlowPresentation(
    var nameOfWireflow: String = "", var buildDate: String,
    var application: String, var version: String
) {
    var flows: MutableMap<String, Wireflow>? = null
}