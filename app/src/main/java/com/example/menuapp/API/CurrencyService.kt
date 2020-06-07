package com.example.menuapp.API

import com.example.menuapp.models.Currency
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    // Get all currencies (base EURO)
    @GET("latest")
    fun getAllCurrencies(): Call<Currency>

    // Get the kuna to euro conversion
    @GET("latest")
    fun getKunaConversion(
        @Query("symbols") symbols: String
    ): Call<Currency>
}