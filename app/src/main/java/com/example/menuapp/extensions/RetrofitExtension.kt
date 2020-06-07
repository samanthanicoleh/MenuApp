package com.example.menuapp.extensions

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.onResult(
    onResponse: (response: Response<T>, data: T?) -> Unit,
    onFailure: (t: Throwable?) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse(response, response.body())
        }
    })
}