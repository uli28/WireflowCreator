package com.uli28.wireflowcreator.wireflows.entities

class FlowPresentation(
    var nameOfWireflow: String = "", var buildDate: String,
    var application: String, var versionName: String,
    var testClassName: String
) {
    var flows: MutableMap<String, Wireflow>? = null
}