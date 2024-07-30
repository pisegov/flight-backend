package com.myaxa.features.state_routing

import com.myaxa.data.database.StateTable
import com.myaxa.data.model.State
import com.myaxa.data.network_client.NetworkClient
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.collections.*
import kotlinx.coroutines.delay
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds

val receivers: MutableSet<ApplicationCall> = ConcurrentSet()

fun Application.configureStateRouting() {

    val networkClient by inject<NetworkClient>()
    routing {
        get("/state") {
            val stateFromDatabase = StateTable.fetch()
            call.respond(stateFromDatabase)
        }

        get("/state/subscribe") {
            receivers.add(call)
            delay(5L.seconds)
            receivers.remove(call)

            call.respond(HttpStatusCode.RequestTimeout)
        }

        post("/state") {
            val newState = call.receive<State>()
            StateTable.insert(newState)

            networkClient.sendSwitchRequest(newState)

            call.respond(newState)

            receivers.forEach { receiver ->
                receiver.respond(newState)
            }
            receivers.clear()
        }
    }
}
