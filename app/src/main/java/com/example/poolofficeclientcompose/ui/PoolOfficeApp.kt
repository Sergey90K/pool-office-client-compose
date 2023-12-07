package com.example.poolofficeclientcompose.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.poolofficeclientcompose.ui.screens.HomeScreen
import com.example.poolofficeclientcompose.ui.screens.PoolOfficeTopAppBar
import com.example.poolofficeclientcompose.ui.screens.PoolOfficeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoolOfficeApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { PoolOfficeTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val poolOfficeViewModel: PoolOfficeViewModel =
                viewModel(factory = PoolOfficeViewModel.Factory)
            HomeScreen(
                poolInfoDataUiState = poolOfficeViewModel.poolInfoDataUiState,
                switchRelay = poolOfficeViewModel::switchRelay,
                reloadAllData = poolOfficeViewModel::getPoolInfo
            )
        }

    }
}