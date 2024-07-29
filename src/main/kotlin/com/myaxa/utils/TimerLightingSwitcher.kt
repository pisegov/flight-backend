package com.myaxa.utils

import com.myaxa.data.database.StateDBO
import com.myaxa.data.database.StateTable
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimerLightingSwitcher(
    private val client: HttpClient,
    private val coroutineScope: CoroutineScope,
) {
    fun setLightingSchedule() {
        timerFlow(1.minutes)
            .map { getNewLightingState() }
            .distinctUntilChanged { old, new -> old.lightingIsOn == new.lightingIsOn }
            .onEach { state ->
                StateTable.insert(state)
                sendSwitchRequest(client = client, state = state)
            }
            .launchIn(coroutineScope)

        timerFlow(10.minutes)
            .onEach {
                val state = StateTable.fetch()
                sendSwitchRequest(client, state)
            }.launchIn(coroutineScope)
    }

    private fun timerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private fun getNewLightingState(): StateDBO {
        val state = StateTable.fetch()
        if (!state.scheduleIsOn) return state

        return when (getCurrentTime()) {
            state.lightingStartTime -> {
                state.copy(lightingIsOn = true)
            }

            state.lightingStopTime -> {
                state.copy(lightingIsOn = false)
            }

            else -> state
        }
    }

    private suspend fun sendSwitchRequest(client: HttpClient, state: StateDBO) {
        val lightingStates = mapOf(true to "on", false to "off")
        val url = lightingStates[state.lightingIsOn] ?: return
        client.get(url)
    }

    private fun getCurrentTime(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return hour * 100 + minute
    }
}
