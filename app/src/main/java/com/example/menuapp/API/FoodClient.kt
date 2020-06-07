package com.example.menuapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodClient {
    companion object {
        // Base url for the API with food/drink/dessert data
        private const val baseUrl = "http://bepy.pro/"
        private lateinit var retrofit: Retrofit

        val client: FoodService
            get() {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(
                    FoodService::class.java
                )
            }
    }
}
