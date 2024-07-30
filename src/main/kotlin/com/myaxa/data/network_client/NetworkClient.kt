package com.myaxa.data.network_client

import com.myaxa.data.model.State
import io.ktor.client.*
import io.ktor.client.request.*

class NetworkClient(private val client: HttpClient) {

    suspend fun sendSwitchRequest(state: State) = safeApiCall {
        val lightingStates = mapOf(true to "on", false to "off")
        val url = lightingStates[state.lightingIsOn] ?: return@safeApiCall
        client.get(url)
    }

    private suspend inline fun safeApiCall(crossinline block: suspend () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            println("ERROR: ${e.message}")
        }
    }
}
