package com.example.poolofficeclientcompose.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.poolofficeclientcompose.ui.screens.HomeScreen
import com.example.poolofficeclientcompose.ui.screens.PoolOfficeTopAppBar
import com.example.poolofficeclientcompose.ui.screens.PoolOfficeViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.poolofficeclientcompose.data.SensorScreenAndName
import com.example.poolofficeclientcompose.data.SwitchScreenAndName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolOfficeApp(
    poolOfficeViewModel: PoolOfficeViewModel =
        viewModel(factory = PoolOfficeViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by poolOfficeViewModel.poolInfoDataUiState.collectAsStateWithLifecycle()
    //val uiState = poolOfficeViewModel.poolInfoDataUiState.collectAsStateWithLifecycle()
    val appearanceSwitch = SwitchScreenAndName.switches
    val appearanceSensor = SensorScreenAndName.sensors
    val refreshState by poolOfficeViewModel.refreshingUiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { PoolOfficeTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeScreen(
                poolInfoDataUiState = uiState,
                switchRelay = poolOfficeViewModel::switchRelay,
                reloadAllData = poolOfficeViewModel::getPoolInfo,
                appearanceSwitch = appearanceSwitch,
                appearanceSensor = appearanceSensor,
                switchAllRelay = poolOfficeViewModel::switchAllRelay,
                refreshState = refreshState
            )
        }
    }
}