package com.myaxa.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StateTable: Table(name = "state") {
    private val zeroId = StateTable.short("zeroId").uniqueIndex()
    private val lightingIsOn = StateTable.bool("lightingIsOn")
    private val scheduleIsOn = StateTable.bool("scheduleIsOn")
    private val lightingStartTime = StateTable.integer("lightingStartTime")
    private val lightingStopTime = StateTable.integer("lightingStopTime")

    fun insert(stateDBO: StateDBO) {
        transaction {
            StateTable.replace {
                it[zeroId] = stateDBO.zeroId
                it[lightingIsOn] = stateDBO.lightingIsOn
                it[scheduleIsOn] = stateDBO.scheduleIsOn
                it[lightingStartTime] = stateDBO.lightingStartTime
                it[lightingStopTime] = stateDBO.lightingStopTime
            }
        }
    }

    fun fetch(): StateDBO {
        return transaction {
            val stateModel = StateTable.select { zeroId.eq(0) }
            val statesList = stateModel.map { query ->
                StateDBO(
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