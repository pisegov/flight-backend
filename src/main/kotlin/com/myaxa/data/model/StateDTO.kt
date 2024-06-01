package com.myaxa.data.model

import com.myaxa.data.database.StateDBO
import kotlinx.serialization.Serializable

@Serializable
data class StateDTO(
    val lightingIsOn: Boolean,
    val scheduleIsOn: Boolean,
    val lightingStartTime: Int,
    val lightingStopTime: Int,
) {
    fun toStateDBO() : StateDBO = StateDBO (
        lightingIsOn = lightingIsOn,
        scheduleIsOn = scheduleIsOn,
        lightingStartTime = lightingStartTime,
        lightingStopTime = lightingStopTime,
    )
}