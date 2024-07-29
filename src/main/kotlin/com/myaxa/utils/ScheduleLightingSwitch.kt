package com.myaxa.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.server.application.*

fun Application.scheduleLightingSwitch() {

    val microcontrollerUrl = environment.config
        .propertyOrNull("ktor.security.microcontroller.url")?.getString() ?: return

    val client = HttpClient(CIO).config {
        defaultRequest { url(microcontrollerUrl) }
    }

    TimerLightingSwitcher(client = client, coroutineScope = this).setLightingSchedule()
}
