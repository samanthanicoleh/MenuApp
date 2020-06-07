package com.example.menuapp.API

import com.example.menuapp.models.Dessert
import com.example.menuapp.models.Drink
import com.example.menuapp.models.Meal
import com.example.menuapp.models.Rating
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query


interface FoodService {

    // Get all
    @GET("meals")
    fun getAllMeals(): Call<List<Meal>>

    @GET("drinks")
    fun getAllDrinks(): Call<List<Drink>>

    @GET("desserts")
    fun getAllDesserts(): Call<List<Dessert>>

    // Get meals by preference
    @GET("mealsbypreference")
    fun getMealsByPreference(
        @Query("preference") type: String
    ): Call<List<Meal>>

    // Get meals by type
    @GET("mealsbytype")
    fun getMealsByType(
        @Query("type") type: String
    ): Call<List<Meal>>

    // Get ratings by type (meal, drink,dessert)
    @GET("ratings")
    fun getRatingsByType(
        @Query("type") type: String
    ): Call<List<Rating>>

    // Put ratings into the db (based on like/dislike)
    @PUT("rate")
    fun addRating(
        @Query("itemid") itemid: Int,
        @Query("type") type: String,
        @Query("upvote") upvote: Boolean
    ): Call<Rating>

}