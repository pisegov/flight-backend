package com.myaxa.data.database

import com.myaxa.data.model.StateDTO
import kotlinx.serialization.Serializable

@Serializable
data class StateDBO(
     val lightingIsOn: Boolean,
     val scheduleIsOn: Boolean,
     val lightingStartTime: Int,
     val lightingStopTime: Int,
     val zeroId: Short = 0
) {
     fun toStateDTO() : StateDTO = StateDTO (
          lightingIsOn = lightingIsOn,
          scheduleIsOn = scheduleIsOn,
          lightingStartTime = lightingStartTime,
          lightingStopTime = lightingStopTime,
     )
}