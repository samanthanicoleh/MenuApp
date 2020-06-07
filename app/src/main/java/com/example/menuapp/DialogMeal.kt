package com.example.menuapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.INVISIBLE
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.menuapp.API.FoodClient
import com.example.menuapp.extensions.onResult
import com.example.menuapp.models.Dessert
import com.example.menuapp.models.Drink
import com.example.menuapp.models.Meal
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_meals.*
import kotlinx.android.synthetic.main.fragment_dialog_meal.*
import kotlinx.android.synthetic.main.fragment_dialog_meal.view.*
import kotlinx.android.synthetic.main.menu_item.*

class DialogMeal(val data: Any) : AppCompatDialogFragment() {

    var large: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.fragment_dialog_meal, null)
            builder.setView(view)
                .setPositiveButton(
                    "Order",
                    DialogInterface.OnClickListener { dialog, id ->
                        if (data is Meal) {
                            if (large) data.price += 1
                            LocalStorage.meals.add(data)

                        } else if (data is Drink) {
                            if (large) data.price += 1
                            LocalStorage.drinks.add(data)

                        } else if (data is Dessert) {
                            if (large) data.price += 1
                            LocalStorage.desserts.add(data)

                        }
                    })


                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })

            view.rbLargePortion.setOnCheckedChangeListener { _, isChecked ->
                large = isChecked
            }

            if (data is Meal) {
                FoodClient.client.getAllDrinks().onResult(
                    onResponse = { _, apiData ->
                        if (apiData != null) {
                            for (tempDrink in apiData) {
                                if (tempDrink.id == data.goesWellWith) {
                                    view.tvDialogRecommendationTitle.text = tempDrink.drink
                                    if (!tempDrink.url.isNullOrEmpty()) {
                                        Picasso.get()
                                            .load(tempDrink.url)
                                            .into(view.ivRecommendation)
                                    }
                                    break
                                }
                            }
                        }
                    },
                    onFailure = {
                        println("There has been an error. ${it?.message}")
                    }
                )
            }
            if (data is Drink) {
                view.tvDialogRecommendationTitle.visibility = INVISIBLE
                view.tvDialogRecommendation.visibility = INVISIBLE
                view.ivRecommendation.visibility = INVISIBLE
            }
            if (data is Dessert) {
                view.tvDialogRecommendationTitle.visibility = INVISIBLE
                view.tvDialogRecommendation.visibility = INVISIBLE
                view.ivRecommendation.visibility = INVISIBLE
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}