package com.example.poolofficeclientcompose.network

data class CombinedData(
    val sensorsData: PoolInfoData,
    val relayData: InitializationStateRelay
)
