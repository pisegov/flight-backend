package com.myaxa.data.database

import kotlinx.serialization.Serializable

@Serializable
data class StateDTO(
     val lightingIsOn: Boolean,
     val scheduleIsOn: Boolean,
     val lightingStartTime: Int,
     val lightingStopTime: Int,
     val zeroId: Short = 0
)