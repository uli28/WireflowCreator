package com.uli28.wireflowcreator.wireflows.entities

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import java.io.File
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    val flowRepresentations =
        createFlowPresentation()
    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
    val json: String = gsonPretty.toJson(flowRepresentations)
    println(json)
    File("bezkoder2.json").writeText(json)
}

@RequiresApi(Build.VERSION_CODES.O)
fun createFlowPresentation(): FlowPresentation {
    val flowPresentation =
        FlowPresentation("nyName", LocalDate.now().toString(),"myApp" , "")

    val requirement =
        TestedRequirement()
    requirement.id = "JIRA-123"
    requirement.link ="https://www.google.at"
    val flow1 =
        Wireflow()
    flow1.testedRequirements = mutableListOf(requirement)

    val image =
        ImageType()
    image.filename = "/resources/Screenshot_1598116116.png"
    image.width = 1440
    image.height = 2560

    val transition =
        TransitionEventType()
    val coordinates =
        Coordinates()
    coordinates.x = 874
    coordinates.y = 1020
    transition.coordinates = coordinates
    flow1.steps = mutableListOf(image, transition)

    val wireflows = mutableMapOf("id" to flow1)
    flowPresentation.flows = wireflows

    return flowPresentation
}