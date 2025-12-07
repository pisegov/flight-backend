package com.myaxa.plugins

import com.myaxa.data.database.StateTable
import com.myaxa.data.model.State
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

private object EnvVars {
    const val FLIGHT_DATABASE_RELATIVE_PATH = "FLIGHT_DATABASE_RELATIVE_PATH"
}

fun Application.configureDatabase() {
    val databasePath = System.getenv(EnvVars.FLIGHT_DATABASE_RELATIVE_PATH)
        ?: error("There is no database path in FLIGHT_DATABASE_RELATIVE_PATH")
    Database.connect("jdbc:sqlite:$databasePath", driver = "org.sqlite.JDBC")

    transaction {
        SchemaUtils.create(StateTable)
        StateTable.insertIfNotExist(
            State(
                lightingIsOn = true,
                scheduleIsOn = true,
                lightingStartTime = 800,
                lightingStopTime = 2200
            )
        )
    }
}
