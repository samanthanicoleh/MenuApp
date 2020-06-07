package com.example.menuapp.models

data class Dessert(
    val id: Int,
    val dessert: String,
    val preference: String,
    var price: Double,
    val url: String
)