package com.example.menuapp.models

data class Drink(
    val id: Int,
    val drink: String,
    val type: String,
    var price: Double,
    val url: String,
    val goesWellWith: Int
)