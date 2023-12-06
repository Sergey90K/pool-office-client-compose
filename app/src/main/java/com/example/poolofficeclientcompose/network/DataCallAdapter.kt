package com.example.poolofficeclientcompose.network

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class DataCallAdapter <R : Any>(private val responseType: Type) :
    CallAdapter<R, Call<NetworkResult<R>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Call<NetworkResult<R>> {
        return NetworkResponseCall(call)
    }
}