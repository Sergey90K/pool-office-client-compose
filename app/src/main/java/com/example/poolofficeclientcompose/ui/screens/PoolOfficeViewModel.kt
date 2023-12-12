package com.example.poolofficeclientcompose.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.poolofficeclientcompose.PoolOfficeApplication
import com.example.poolofficeclientcompose.data.PoolOfficeRepository
import com.example.poolofficeclientcompose.network.CombinedData
import com.example.poolofficeclientcompose.network.InitializationStateRelay
import com.example.poolofficeclientcompose.network.NetworkResult
import com.example.poolofficeclientcompose.network.PoolInfoData
import com.example.poolofficeclientcompose.network.RelayData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

const val RELAY_ID_ALL = 256

sealed interface PoolOfficeUiState {
    data class Success(val combineData: CombinedData) : PoolOfficeUiState
    data class Error(val errorCode: Int = 1) : PoolOfficeUiState
    object Loading : PoolOfficeUiState
}

class PoolOfficeViewModel(private val poolOfficeRepository: PoolOfficeRepository) : ViewModel() {
    private val errorProgram = 1
    private val errorSensor = 2
    private val errorRelay = 3
    private val errorLoading = 4
    private val errorData = 5

    private val _poolInfoDataUiState =
        MutableStateFlow<PoolOfficeUiState>(PoolOfficeUiState.Loading)

    var poolInfoDataUiState: StateFlow<PoolOfficeUiState> = _poolInfoDataUiState.asStateFlow()

    init {
        getPoolInfo()
    }

    fun getPoolInfo() {
        viewModelScope.launch {
            _poolInfoDataUiState.getAndUpdate {
                PoolOfficeUiState.Loading
                try {
                    val sensorsData = poolOfficeRepository.getSensorData()
                    when (sensorsData) {
                        is NetworkResult.Success -> {
                            val relayData = poolOfficeRepository.getInitializationState()
                            when (relayData) {
                                is NetworkResult.Success -> {
                                    sensorsData.bodyData as PoolInfoData
                                    relayData.bodyData as InitializationStateRelay
                                    if (sensorsData.bodyData.errorCode != errorSensor) {
                                        if (relayData.bodyData.errorCode != errorRelay) {
                                            PoolOfficeUiState.Success(
                                                CombinedData(
                                                    sensorsData.bodyData,
                                                    relayData.bodyData
                                                )
                                            )
                                        } else {
                                            PoolOfficeUiState.Error(errorRelay)
                                        }
                                    } else {
                                        PoolOfficeUiState.Error(errorSensor)
                                    }
                                }

                                is NetworkResult.Exception ->
                                    PoolOfficeUiState.Error(errorLoading)

                                is NetworkResult.Error ->
                                    PoolOfficeUiState.Error(errorData)
                            }
                        }

                        is NetworkResult.Exception ->
                            PoolOfficeUiState.Error(errorLoading)

                        is NetworkResult.Error ->
                            PoolOfficeUiState.Error(errorData)
                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error(errorProgram)
                }
            }
        }
    }

    fun switchRelay(relayId: Int, relayState: Boolean) {
        viewModelScope.launch {
            _poolInfoDataUiState.update {
                try {
                    val state = if (relayState) 1 else 0
                    val relaySwitchState = poolOfficeRepository.switchRelay(relayId, state)
                    when (relaySwitchState) {
                        is NetworkResult.Success -> {
                            val rezRelaySwitchState = relaySwitchState.bodyData as RelayData
                            if (rezRelaySwitchState.relayNumber ==
                                relayId && rezRelaySwitchState.errorCode !=
                                errorRelay && rezRelaySwitchState.stateRelay ==
                                relayState
                            ) {
                                when (_poolInfoDataUiState.value) {
                                    is PoolOfficeUiState.Success -> {
                                        val sensorsData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.sensorsData
                                        val relaysData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.relayData
                                        relaysData.relayAnswer[relayId] = relayState
                                        PoolOfficeUiState.Success(
                                            CombinedData(
                                                sensorsData,
                                                relaysData
                                            )
                                        )
                                    }

                                    else -> {
                                        PoolOfficeUiState.Error(errorProgram)
                                    }
                                }

                            } else {
                                PoolOfficeUiState.Error(errorRelay)
                            }

                        }

                        is NetworkResult.Exception -> {
                            PoolOfficeUiState.Error(errorLoading)
                        }

                        is NetworkResult.Error -> {
                            PoolOfficeUiState.Error(errorData)
                        }
                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error(errorProgram)
                }
            }
        }
    }

    fun switchAllRelay(relayState: Boolean) {
        viewModelScope.launch {
            _poolInfoDataUiState.update {
                try {
                    val state = if (relayState) 1 else 0
                    val relaySwitchState = poolOfficeRepository.switchRelay(RELAY_ID_ALL, state)
                    when (relaySwitchState) {
                        is NetworkResult.Success -> {
                            val rezRelaySwitchState = relaySwitchState.bodyData as RelayData
                            if (rezRelaySwitchState.relayNumber ==
                                RELAY_ID_ALL && rezRelaySwitchState.errorCode !=
                                errorRelay && rezRelaySwitchState.stateRelay ==
                                relayState
                            ) {
                                when (_poolInfoDataUiState.value) {
                                    is PoolOfficeUiState.Success -> {
                                        val sensorsData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.sensorsData
                                        var relaysData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.relayData
                                        relaysData.relayAnswer = Array(8) { relayState }
                                        PoolOfficeUiState.Success(
                                            CombinedData(
                                                sensorsData,
                                                relaysData
                                            )
                                        )
                                    }

                                    else -> {
                                        PoolOfficeUiState.Error(errorProgram)
                                    }
                                }

                            } else {
                                PoolOfficeUiState.Error(errorRelay)
                            }

                        }

                        is NetworkResult.Exception -> {
                            PoolOfficeUiState.Error(errorLoading)
                        }

                        is NetworkResult.Error -> {
                            PoolOfficeUiState.Error(errorData)
                        }
                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error(errorProgram)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PoolOfficeApplication)
                val poolOfficeRepository = application.container.poolOfficeRepository
                PoolOfficeViewModel(poolOfficeRepository = poolOfficeRepository)
            }
        }
    }
}