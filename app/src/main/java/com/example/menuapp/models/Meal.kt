package com.example.menuapp.models


data class Meal(
    val id: Int,
    val meal: String,
    val type: String,
    val preference: String,
    var price: Double,
    val url: String,
    val goesWellWith: Int
)