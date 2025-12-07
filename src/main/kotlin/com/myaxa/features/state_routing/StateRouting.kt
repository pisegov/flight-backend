package com.myaxa.features.state_routing

import com.myaxa.data.model.State
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds

fun Application.configureStateRouting() {

    val stateStore by inject<StateStore> { parametersOf(this) }
    val callStore by inject<CallStore> { parametersOf(this) }

    suspend fun ApplicationCall.sendTimeoutOnNoResponse() {
        delay(5.seconds)
        if (callStore.contains(this)) {
            callStore.remove(this)
            respond(stateStore.currentState.value)
        }
    }

    routing {
        get("/state") {
            val currentState = stateStore.currentState.value
            call.respond(currentState)
        }

        get("/state/subscribe") {
            callStore.add(call)
            call.sendTimeoutOnNoResponse()
        }

        post("/state") {
            val newState = call.receive<State>()
            callStore.add(call)
            stateStore.updateState(newState)
            call.sendTimeoutOnNoResponse()
        }
    }
}
