package com.myaxa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val lightingIsOn: Boolean,
    val scheduleIsOn: Boolean,
    val lightingStartTime: Int,
    val lightingStopTime: Int,
)