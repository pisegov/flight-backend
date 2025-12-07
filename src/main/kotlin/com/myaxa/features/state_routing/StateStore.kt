package com.myaxa.features.state_routing

import com.myaxa.data.database.StateTable
import com.myaxa.data.model.State
import com.myaxa.data.network_client.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StateStore(
    private val coroutineScope: CoroutineScope,
    private val networkClient: NetworkClient,
) {

    private val _currentState = MutableStateFlow(StateTable.fetch())
    val currentState = _currentState.asStateFlow()

    init {
        coroutineScope.launch {
            currentState.collect { state ->
                StateTable.insert(state)
                networkClient.sendSwitchRequest(state)
            }
        }
    }

    fun updateState(state: State) = _currentState.update { state }

    fun setLighting(isOn: Boolean) = _currentState.update {
        it.copy(lightingIsOn = isOn)
    }

    fun resendSwitchRequest() {
        coroutineScope.launch {
            networkClient.sendSwitchRequest(currentState.value)
        }
    }
}