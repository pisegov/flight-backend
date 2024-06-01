package com.myaxa.utils

import com.myaxa.data.database.StateTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import java.util.*
import kotlin.time.DurationUnit

fun Application.scheduleTasks() {
    val microcontrollerUrl = environment.config
        .propertyOrNull("ktor.security.microcontroller.url")?.getString() ?: return

    Scheduler {
        val state = StateTable.fetch()
        if (!state.scheduleIsOn) return@Scheduler

        val switchLighting: suspend (Boolean) -> Unit = { isLightingOn ->
            val newState = state.copy(lightingIsOn = isLightingOn)
            StateTable.insert(newState)

            sendSwitchRequest(microcontrollerUrl, isLightingOn)
        }

        val time = getCurrentTime()

        when (time) {
            state.lightingStartTime -> {
                switchLighting(true)
            }

            state.lightingStopTime -> {
                switchLighting(false)
            }

            else -> Unit
        }

    }.scheduleExecution(Every(50, DurationUnit.SECONDS))

    Scheduler {
        val state = StateTable.fetch()
        sendSwitchRequest(microcontrollerUrl, state.lightingIsOn)
    }.scheduleExecution(Every(10, DurationUnit.MINUTES))
}

fun getCurrentTime(): Int {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return hour * 100 + minute
}

suspend fun sendSwitchRequest (microcontrollerUrl: String, isLightingOn: Boolean) {
    val client = HttpClient(CIO)
    val lightingStates = mapOf(true to "on", false to "off")
    client.get("$microcontrollerUrl${lightingStates[isLightingOn]}")
}