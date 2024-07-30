package com.myaxa.plugins

import com.myaxa.data.database.StateTable
import com.myaxa.data.model.State
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    Database.connect(
        url = environment.config
            .propertyOrNull("ktor.security.database.mysqlUrl")?.getString() ?: "",
        driver = "com.mysql.cj.jdbc.Driver",
        user = environment.config
            .propertyOrNull("ktor.security.database.mysqlUser")?.getString() ?: "",
        password = environment.config
            .propertyOrNull("ktor.security.database.mysqlPassword")?.getString() ?: ""
    )

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
