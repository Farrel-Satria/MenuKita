package com.example.menukita.model

import androidx.annotation.DrawableRes

data class BannerItem(
    val tag: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int
)
