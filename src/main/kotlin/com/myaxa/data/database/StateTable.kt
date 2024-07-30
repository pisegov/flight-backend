package com.myaxa.data.database

import com.myaxa.data.model.State
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StateTable : Table(name = "state") {
    private const val STATE_ID: Short = 0
    private val zeroId = StateTable.short("zeroId").uniqueIndex()
    private val lightingIsOn = StateTable.bool("lightingIsOn")
    private val scheduleIsOn = StateTable.bool("scheduleIsOn")
    private val lightingStartTime = StateTable.integer("lightingStartTime")
    private val lightingStopTime = StateTable.integer("lightingStopTime")

    fun insert(state: State) {
        transaction {
            StateTable.replace {
                it[zeroId] = STATE_ID
                it[lightingIsOn] = state.lightingIsOn
                it[scheduleIsOn] = state.scheduleIsOn
                it[lightingStartTime] = state.lightingStartTime
                it[lightingStopTime] = state.lightingStopTime
            }
        }
    }

    fun fetch(): StateDBO {
        return transaction {
            val stateModel = StateTable.select { zeroId.eq(STATE_ID) }
            val statesList = stateModel.map { query ->
                State(
                    lightingIsOn = query[lightingIsOn],
                    scheduleIsOn = query[scheduleIsOn],
                    lightingStartTime = query[lightingStartTime],
                    lightingStopTime = query[lightingStopTime]
                )
            }
            // we store only one state
            statesList.single()
        }
    }
}