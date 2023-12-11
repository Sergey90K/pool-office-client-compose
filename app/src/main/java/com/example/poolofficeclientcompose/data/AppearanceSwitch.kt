package com.example.poolofficeclientcompose.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AppearanceSwitch(
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
)
