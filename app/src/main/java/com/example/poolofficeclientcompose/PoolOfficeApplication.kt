package com.example.poolofficeclientcompose

import android.app.Application
import com.example.poolofficeclientcompose.data.AppContainer
import com.example.poolofficeclientcompose.data.DefaultAppContainer

class PoolOfficeApplication :  Application(){
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}