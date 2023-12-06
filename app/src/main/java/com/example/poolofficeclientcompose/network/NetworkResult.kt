package com.example.poolofficeclientcompose.network

sealed class NetworkResult <R:Any>{
    class Success<R: Any>(val bodyData : Any) : NetworkResult<R>(){}
    class Error<R: Any>(val code : Int, val message: String?) : NetworkResult<R>(){}
    class Exception<R: Any>(val e: Throwable) : NetworkResult<R>(){}

}
