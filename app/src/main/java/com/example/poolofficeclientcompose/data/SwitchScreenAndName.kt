package com.example.poolofficeclientcompose.data

import com.example.poolofficeclientcompose.R

object SwitchScreenAndName {
    val switches = listOf(
        AppearanceSwitch(
            nameRes = R.string.termostat,
            descriptionRes = R.string.thermostat_control,
            imageRes = R.drawable.thermostat_black_48dp
        ),
        AppearanceSwitch(
            nameRes = R.string.pump,
            descriptionRes = R.string.management_of_pumps,
            imageRes = R.drawable.rowing_black_48dp
        ),
        AppearanceSwitch(
            nameRes = R.string.heating,
            descriptionRes = R.string.heating_control,
            imageRes = R.drawable.dew_point_black_48dp
        ),
        AppearanceSwitch(
            nameRes = R.string.cover,
            descriptionRes = R.string.cover_management,
            imageRes = R.drawable.waves_black_48dp
        )
        , AppearanceSwitch(
            nameRes = R.string.lighting,
            descriptionRes = R.string.lighting_control,
            imageRes = R.drawable.model_training_black_48dp
        ),
        AppearanceSwitch(
            nameRes = R.string.electricity,
            descriptionRes = R.string.electricity_management,
            imageRes = R.drawable.offline_bolt_black_48dp
        ),
        AppearanceSwitch(
            nameRes = R.string.cooling,
            descriptionRes = R.string.management_of_pumps,
            imageRes = R.drawable.severe_cold_black_48dp
        ),
        AppearanceSwitch(
            nameRes = R.string.water,
            descriptionRes = R.string.water_management,
            imageRes = R.drawable.water_drop_black_48dp
        )
    )
}
