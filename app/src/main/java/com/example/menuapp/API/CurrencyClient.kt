package com.example.menuapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyClient {
    companion object {
        // Base URL for the currency API
        private const val baseUrl = "https://api.exchangeratesapi.io/"
        private lateinit var retrofit: Retrofit

        val client: CurrencyService
            get() {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                return retrofit.create(
                    CurrencyService::class.java
                )
            }
    }
}