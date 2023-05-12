package com.myaxa.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StateTable: Table(name = "state") {
    private val zeroId = StateTable.short("zeroId").uniqueIndex()
    private val lightingIsOn = StateTable.bool("lightingIsOn")
    private val scheduleIsOn = StateTable.bool("scheduleIsOn")
    private val lightingStartTime = StateTable.integer("lightingStartTime")
    private val lightingStopTime = StateTable.integer("lightingStopTime")

    fun insert(stateDTO: StateDTO) {
        transaction {
            StateTable.replace {
                it[zeroId] = stateDTO.zeroId
                it[lightingIsOn] = stateDTO.lightingIsOn
                it[scheduleIsOn] = stateDTO.scheduleIsOn
                it[lightingStartTime] = stateDTO.lightingStartTime
                it[lightingStopTime] = stateDTO.lightingStopTime
            }
        }
    }

    fun fetch(): StateDTO {
        return transaction {
            val stateModel = StateTable.select { zeroId.eq(0) }
            val statesList = stateModel.map { query ->
                StateDTO(
                    zeroId = query[zeroId],
                    lightingIsOn = query[lightingIsOn],
                    scheduleIsOn = query[scheduleIsOn],
                    lightingStartTime = query[lightingStartTime],
                    lightingStopTime = query[lightingStopTime]
                )
            }
            // we store only one state
            statesList[0]
        }
    }
}