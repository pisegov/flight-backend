package com.myaxa.features.state_routing

import com.myaxa.data.model.State
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.collections.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CallStore(
    coroutineScope: CoroutineScope,
    stateStore: StateStore,
) {

    private val receivers: MutableSet<ApplicationCall> = ConcurrentSet()

    init {
        coroutineScope.launch {
            stateStore.currentState.collect { state ->
                respondForAll(state)
            }
        }
    }

    fun add(call: ApplicationCall) = receivers.add(call)

    fun contains(call: ApplicationCall): Boolean = receivers.contains(call)

    fun remove(call: ApplicationCall) = receivers.remove(call)

    private suspend fun respondForAll(state: State) {
        receivers.forEach { call ->
            call.respond(state)
        }
        receivers.clear()
    }
}