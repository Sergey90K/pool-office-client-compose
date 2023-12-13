package com.example.poolofficeclientcompose.ui.screens

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.poolofficeclientcompose.PoolOfficeApplication
import com.example.poolofficeclientcompose.R
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
    data class Error(@StringRes val errorCode: Int = R.string.error_initialization) :
        PoolOfficeUiState

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
                                            PoolOfficeUiState.Error(getErrorDescription(errorRelay))
                                        }
                                    } else {
                                        PoolOfficeUiState.Error(getErrorDescription(errorSensor))
                                    }
                                }

                                is NetworkResult.Exception ->
                                    PoolOfficeUiState.Error(getErrorDescription(errorLoading))

                                is NetworkResult.Error ->
                                    PoolOfficeUiState.Error(getErrorDescription(errorData))
                            }
                        }

                        is NetworkResult.Exception ->
                            PoolOfficeUiState.Error(getErrorDescription(errorLoading))

                        is NetworkResult.Error ->
                            PoolOfficeUiState.Error(getErrorDescription(errorData))
                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error(getErrorDescription(errorProgram))
                }
            }
        }
    }

    fun switchRelay(relayId: Int, relayState: Boolean) {
        switchForRelay(relayId, relayState)
    }

    fun switchAllRelay(relayState: Boolean) {
       switchForRelay(RELAY_ID_ALL, relayState)
    }

    private fun switchForRelay(relayId: Int, relayState: Boolean) {
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
                                        if (relayId == RELAY_ID_ALL) {
                                            relaysData.relayAnswer = Array(8) { relayState }
                                        } else {
                                            relaysData.relayAnswer[relayId] = relayState
                                        }
                                        PoolOfficeUiState.Success(
                                            CombinedData(
                                                sensorsData,
                                                relaysData
                                            )
                                        )
                                    }

                                    else -> {
                                        PoolOfficeUiState.Error(getErrorDescription(errorProgram))
                                    }
                                }

                            } else {
                                PoolOfficeUiState.Error(getErrorDescription(errorRelay))
                            }

                        }

                        is NetworkResult.Exception -> {
                            PoolOfficeUiState.Error(getErrorDescription(errorLoading))
                        }

                        is NetworkResult.Error -> {
                            PoolOfficeUiState.Error(getErrorDescription(errorData))
                        }
                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error(getErrorDescription(errorProgram))
                }
            }
        }
    }

    private fun getErrorDescription(errorCode: Int): Int {
        when (errorCode) {
            errorProgram -> {
                return R.string.error_in_program
            }

            errorSensor -> {
                return R.string.error_in_sensor
            }

            errorRelay -> {
                return R.string.error_in_relay
            }

            errorLoading -> {
                return R.string.error_in_loading
            }

            errorData -> {
                return R.string.error_in_data
            }

            else -> {
                return R.string.unknown_error
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