package com.myaxa.plugins

import com.myaxa.data.database.StateDTO
import com.myaxa.data.database.StateTable
import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val database = Database.connect(
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
        StateTable.insert(StateDTO(
            zeroId = 0,
            lightingIsOn = false,
            scheduleIsOn = true,
            lightingStartTime = 1000,
            lightingStopTime = 2200
        ))
    }
}
