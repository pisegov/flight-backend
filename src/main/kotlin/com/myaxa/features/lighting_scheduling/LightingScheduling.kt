package com.myaxa.features.lighting_scheduling

import io.ktor.server.application.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

fun Application.scheduleLightingSwitch() {
    val timerLightingSwitcher by inject<TimerLightingSwitcher> { parametersOf(this) }
    timerLightingSwitcher.setLightingSchedule()
}
