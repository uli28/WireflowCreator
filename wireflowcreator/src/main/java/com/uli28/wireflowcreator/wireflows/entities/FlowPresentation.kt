package com.uli28.wireflowcreator.wireflows.entities

class FlowPresentation(var name: String = "", var date: String?, var application: String?) {
    var flows: MutableMap<String, Wireflow>? = null
}