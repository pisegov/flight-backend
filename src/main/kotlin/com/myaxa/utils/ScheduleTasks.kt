package com.myaxa.utils

import com.myaxa.data.database.StateTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

fun Application.scheduleTasks() {
    val microcontrollerUrl = environment.config
        .propertyOrNull("ktor.security.microcontroller.url")?.getString() ?: ""
    Scheduler {
        val client = HttpClient(CIO)

        val previousState = StateTable.fetch()

        val lightingStates = mapOf(
            true to "on",
            false to "off",
        )

        if (previousState.scheduleIsOn) {
            val calendar = Calendar.getInstance()

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val time = hour * 100 + minute

            if (time == previousState.lightingStartTime) {
                StateTable.insert(previousState.copy(lightingIsOn = true))
                CoroutineScope(Dispatchers.Default).launch {
                    client.get("$microcontrollerUrl${lightingStates[true]}")
                }
            }
            else if (time == previousState.lightingStopTime) {
                StateTable.insert(previousState.copy(lightingIsOn = false))
                CoroutineScope(Dispatchers.Default).launch {
                    client.get("$microcontrollerUrl${lightingStates[false]}")
                }
            }
        }
    }.scheduleExecution(Every(1, TimeUnit.MINUTES))

    Scheduler {
        val client = HttpClient(CIO)
        CoroutineScope(Dispatchers.Default).launch{
            client.get(microcontrollerUrl)
        }
    }.scheduleExecution(Every(5, TimeUnit.MINUTES))
}