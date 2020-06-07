package com.example.menuapp.models

data class Currency(
    val rates: HashMap<String, Double>,
    val base: String,
    val date: String
)