package com.myaxa.features.lighting_scheduling

import com.myaxa.data.model.State
import com.myaxa.features.state_routing.StateStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimerLightingSwitcher(
    private val coroutineScope: CoroutineScope,
    private val stateStore: StateStore,
) {
    fun setLightingSchedule() {
        timerFlow(1.minutes)
            .combine(stateStore.currentState) { _, state -> state }
            .map { getNewLightingState(it) }
            .onEach { state ->
                stateStore.setLighting(state.lightingIsOn)
            }
            .launchIn(coroutineScope)

        timerFlow(10.minutes)
            .onEach { stateStore.resendSwitchRequest() }
            .launchIn(coroutineScope)
    }

    private fun timerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private fun getNewLightingState(state: State): State {
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
