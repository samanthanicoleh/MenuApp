package com.example.menuapp

import com.example.menuapp.models.Dessert
import com.example.menuapp.models.Drink
import com.example.menuapp.models.Meal

class LocalStorage {
    companion object {
        // Local Storage for orders to get final price
        var meals: ArrayList<Meal> = ArrayList<Meal>()
        var desserts: ArrayList<Dessert> = ArrayList<Dessert>()
        var drinks: ArrayList<Drink> = ArrayList<Drink>()
    }
}