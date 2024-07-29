package com.myaxa

import com.myaxa.features.state.configureStateRouting
import com.myaxa.plugins.configureDatabase
import com.myaxa.plugins.configureRouting
import com.myaxa.plugins.configureSerialization
import com.myaxa.utils.scheduleLightingSwitch
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureStateRouting()
    configureRouting()
    configureDatabase()
    configureSerialization()
    scheduleLightingSwitch()
}