package com.uli28.wireflowcreator.wireflows.entities

class FlowPresentation(
    var name: String = "", var buildDate: String,
    var application: String, var versionName: String) {
    var flows: MutableMap<String, Wireflow>? = null
}