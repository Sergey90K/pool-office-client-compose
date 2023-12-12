package com.example.poolofficeclientcompose.ui.screens

import android.util.Log
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
    object Error : PoolOfficeUiState
    object Loading : PoolOfficeUiState
}

class PoolOfficeViewModel(private val poolOfficeRepository: PoolOfficeRepository) : ViewModel() {

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
                                    PoolOfficeUiState.Success(
                                        CombinedData(
                                            sensorsData.bodyData as PoolInfoData,
                                            relayData.bodyData as InitializationStateRelay
                                        )
                                    )
                                }

                                is NetworkResult.Exception ->
                                    PoolOfficeUiState.Error

                                is NetworkResult.Error ->
                                    PoolOfficeUiState.Error
                            }
                        }

                        is NetworkResult.Exception ->
                            PoolOfficeUiState.Error

                        is NetworkResult.Error ->
                            PoolOfficeUiState.Error

                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error
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
                            Log.d("Debug", "1")
                            val rezRelaySwitchState = relaySwitchState.bodyData as RelayData
                            if (rezRelaySwitchState.relayNumber ==
                                relayId && rezRelaySwitchState.errorCode ==
                                0 && rezRelaySwitchState.stateRelay ==
                                relayState
                            ) {
                                Log.d("Debug", "3")
                                when (_poolInfoDataUiState.value) {
                                    is PoolOfficeUiState.Success -> {
                                        Log.d("Debug", "4")
                                        val sensorsData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.sensorsData
                                        Log.d("Debug", sensorsData.toString())
                                        val relaysData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.relayData
                                        Log.d("Debug", relaysData.relayAnswer.size.toString())
                                        relaysData.relayAnswer[relayId] = relayState
                                        PoolOfficeUiState.Success(
                                            CombinedData(
                                                sensorsData,
                                                relaysData
                                            )
                                        )
                                    }

                                    else -> {
                                        Log.d("Debug", "5")
                                        PoolOfficeUiState.Error
                                    }
                                }

                            } else {
                                Log.d("Debug", "6")
                                PoolOfficeUiState.Error
                            }

                        }

                        is NetworkResult.Exception -> {
                            Log.d("Debug", "7")
                            PoolOfficeUiState.Error
                        }

                        is NetworkResult.Error -> {
                            Log.d("Debug", "8")
                            PoolOfficeUiState.Error
                        }
                    }
                } catch (e: IOException) {
                    Log.d("Debug", "9")
                    PoolOfficeUiState.Error
                }
            }
        }
    }

    fun switchAllRelay(relayState: Boolean){
        viewModelScope.launch {
            _poolInfoDataUiState.update {
                try {
                    val state = if (relayState) 1 else 0
                    val relaySwitchState = poolOfficeRepository.switchRelay(RELAY_ID_ALL, state)
                    when (relaySwitchState) {
                        is NetworkResult.Success -> {
                            val rezRelaySwitchState = relaySwitchState.bodyData as RelayData
                            if (rezRelaySwitchState.relayNumber ==
                                RELAY_ID_ALL && rezRelaySwitchState.errorCode ==
                                0 && rezRelaySwitchState.stateRelay ==
                                relayState
                            ) {
                                when (_poolInfoDataUiState.value) {
                                    is PoolOfficeUiState.Success -> {
                                        val sensorsData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.sensorsData
                                        var relaysData =
                                            (_poolInfoDataUiState.value as PoolOfficeUiState.Success).combineData.relayData
                                        relaysData.relayAnswer = Array(8){relayState}
                                        PoolOfficeUiState.Success(
                                            CombinedData(
                                                sensorsData,
                                                relaysData
                                            )
                                        )
                                    }

                                    else -> {
                                        PoolOfficeUiState.Error
                                    }
                                }

                            } else {
                                PoolOfficeUiState.Error
                            }

                        }

                        is NetworkResult.Exception -> {
                            PoolOfficeUiState.Error
                        }

                        is NetworkResult.Error -> {
                            PoolOfficeUiState.Error
                        }
                    }
                } catch (e: IOException) {
                    PoolOfficeUiState.Error
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