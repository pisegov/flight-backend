package com.myaxa.features.lighting_scheduling

import com.myaxa.data.database.StateTable
import com.myaxa.data.model.State
import com.myaxa.data.network_client.NetworkClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimerLightingSwitcher(
    private val client: NetworkClient,
    private val coroutineScope: CoroutineScope,
) {
    fun setLightingSchedule() {
        timerFlow(1.minutes)
            .map { getNewLightingState() }
            .distinctUntilChanged { old, new -> old.lightingIsOn == new.lightingIsOn }
            .onEach { state ->
                StateTable.insert(state)
                client.sendSwitchRequest(state = state)
            }
            .launchIn(coroutineScope)

        timerFlow(10.minutes)
            .onEach {
                val state = StateTable.fetch()
                client.sendSwitchRequest(state = state)
            }.launchIn(coroutineScope)
    }

    private fun timerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private fun getNewLightingState(): State {
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

    private fun getCurrentTime(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return hour * 100 + minute
    }
}
