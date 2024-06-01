package com.myaxa.features.state

import com.myaxa.data.database.StateTable
import com.myaxa.data.model.StateDTO
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.collections.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

val receivers: MutableSet<ApplicationCall> = ConcurrentSet()

fun Application.configureStateRouting() {
    routing {
        get("/state") {
            val stateFromDatabase = StateTable.fetch()
            call.respond(stateFromDatabase.toStateDTO())
        }

        get("/state/subscribe") {
            receivers.add(call)
            delay(5L.seconds)
            receivers.remove(call)

            call.respond(HttpStatusCode.RequestTimeout)
        }

        post("/state") {

            val newState = call.receive<StateDTO>()
            StateTable.insert(newState.toStateDBO())

            val client = HttpClient(CIO)
            val microcontrollerUrl = this@routing.environment?.config
                ?.propertyOrNull("ktor.security.microcontroller.url")?.getString() ?: ""
            val lightingStates = mapOf(true to "on", false to "off")
            client.get("$microcontrollerUrl${lightingStates[newState.lightingIsOn]}")

            call.respond(newState)

            receivers.forEach { receiver ->
                receiver.respond(newState)
            }
            receivers.clear()
        }
    }
}
