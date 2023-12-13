package com.example.poolofficeclientcompose.data

import com.example.poolofficeclientcompose.R

object SensorScreenAndName {
    val sensors = listOf(
        AppearanceSensor(
            nameRes = R.string.thermometer,
            descriptionRes = R.string.air_temperature,
            imageRes = R.drawable.termometr
        ),
        AppearanceSensor(
            nameRes = R.string.sensor,
            descriptionRes = R.string.water_temperature,
            imageRes = R.drawable.termometr_inner
        ),
        AppearanceSensor(
            nameRes = R.string.temperature,
            descriptionRes = R.string.temperature_in_the_house,
            imageRes = R.drawable.termometr_ulichnyj_fasadnyj
        ),
        AppearanceSensor(
            nameRes = R.string.pump,
            descriptionRes = R.string.pump_indicators,
            imageRes = R.drawable.pump
        )
    )
}