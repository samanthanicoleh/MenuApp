package com.example.menuapp.models

data class Rating(
    val id: Int,
    val itemId: Int,
    val type: String,
    var rating: Int
)