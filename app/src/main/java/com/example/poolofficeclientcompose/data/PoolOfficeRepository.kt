package com.example.poolofficeclientcompose.data

import com.example.poolofficeclientcompose.network.InitializationStateRelay
import com.example.poolofficeclientcompose.network.NetworkResult
import com.example.poolofficeclientcompose.network.PoolInfoData
import com.example.poolofficeclientcompose.network.PoolOfficeApiService
import com.example.poolofficeclientcompose.network.RelayData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PoolOfficeRepository {
    suspend fun getSensorData(): NetworkResult<PoolInfoData>

    @POST("relay/{relayId}/{state}")
    suspend fun switchRelay(
        @Path("relayId") relayId: Int,
        @Path("state") state: Int
    ): NetworkResult<RelayData>

    @GET("/relay")
    suspend fun getInitializationState(): NetworkResult<InitializationStateRelay>
}

class NetworkPoolOfficeRepository(private val poolOfficeApiService: PoolOfficeApiService) :
    PoolOfficeRepository {
    override suspend fun getSensorData(): NetworkResult<PoolInfoData> {
        return poolOfficeApiService.getSensorData()
    }

    override suspend fun switchRelay(relayId: Int, state: Int): NetworkResult<RelayData> {
        return poolOfficeApiService.switchRelay(relayId, state)
    }

    override suspend fun getInitializationState(): NetworkResult<InitializationStateRelay> {
        return poolOfficeApiService.getInitializationState()
    }
}