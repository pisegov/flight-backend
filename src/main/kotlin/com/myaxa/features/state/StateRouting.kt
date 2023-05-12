package com.myaxa.features.state

import com.myaxa.data.database.StateTable
import com.myaxa.data.database.StateDTO
import com.myaxa.data.model.State
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*

fun Application.configureStateRouting() {
    routing {
        get("/state") {
            val stateFromDatabase = StateTable.fetch()
            call.respond(State (
                lightingIsOn = stateFromDatabase.lightingIsOn,
                scheduleIsOn = stateFromDatabase.scheduleIsOn,
                lightingStartTime = stateFromDatabase.lightingStartTime,
                lightingStopTime = stateFromDatabase.lightingStopTime,
            ))
        }

        post("/state") {

            val client = HttpClient(CIO)

            val receive = call.receive<State>()

            val microcontrollerUrl = this@routing.environment?.config
                ?.propertyOrNull("ktor.security.microcontroller.url")?.getString() ?: ""

            StateTable.insert(StateDTO(
                lightingIsOn = receive.lightingIsOn,
                scheduleIsOn = receive.scheduleIsOn,
                lightingStartTime = receive.lightingStartTime,
                lightingStopTime = receive.lightingStopTime
            ))
            val lightingStates = mapOf<Boolean, String>(
                true to "on",
                false to "off",
            )
            val espResponse = client.get("$microcontrollerUrl${lightingStates[receive.lightingIsOn]}")
            call.respond(receive)
        }
    }
}
