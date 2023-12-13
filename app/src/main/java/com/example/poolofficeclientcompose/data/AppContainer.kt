package com.example.poolofficeclientcompose.data

import com.example.poolofficeclientcompose.network.DataCallAdapterFactory
import com.example.poolofficeclientcompose.network.PoolOfficeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val poolOfficeRepository: PoolOfficeRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(DataCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitService by lazy {
        retrofit.create(PoolOfficeApiService::class.java)
    }

    override val poolOfficeRepository: PoolOfficeRepository by lazy {
        NetworkPoolOfficeRepository(retrofitService)
    }
}