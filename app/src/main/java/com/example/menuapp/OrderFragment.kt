package com.example.menuapp

import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebStorage
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.menuapp.models.Dessert
import com.example.menuapp.models.Drink
import com.example.menuapp.models.Meal
import kotlinx.android.synthetic.main.fragment_order.view.*
import java.math.RoundingMode
import java.text.DecimalFormat


class OrderFragment : Fragment() {

    var finalPrice: Double = 0.0
    lateinit var dialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_order, container, false)
        view.tvOrderList.setMovementMethod(ScrollingMovementMethod())


        var mealsToPrint = ""
        for (meal in LocalStorage.meals) {
            mealsToPrint += meal.meal + "     " + meal.price + " €\n\n"
            finalPrice += meal.price
        }
        for (drink in LocalStorage.drinks) {
            mealsToPrint += drink.drink + "     " + drink.price + " €\n\n"
            finalPrice += drink.price
        }
        for (dessert in LocalStorage.desserts) {
            mealsToPrint += dessert.dessert + "     " + dessert.price + " €\n\n"
            finalPrice += dessert.price
        }

        view.tvOrderList.text = mealsToPrint
        view.tvFinalPrice.text = finalPrice.toString() + "€"


        //add discount
        view.btnGetDiscount.setOnClickListener {
            var discountPrice: Double = 0.0
            if (finalPrice < 10) {
                val toast = Toast.makeText(
                    context,
                    "There is no discount for this order.",
                    Toast.LENGTH_SHORT
                )
                toast.view.background.setTintList(context?.let { it1 ->
                    ContextCompat.getColorStateList(
                        it1, android.R.color.holo_blue_bright
                    )
                })
                toast.show()

            }
            if (finalPrice >= 10 && finalPrice < 20) {
                discountPrice = finalPrice * 0.95
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                view.tvFinalPrice.text = df.format(discountPrice).toString() + "€"
            }
            if (finalPrice >= 20 && finalPrice < 30) {
                discountPrice = finalPrice * 0.9
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                view.tvFinalPrice.text = df.format(discountPrice).toString() + "€"            }
            if (finalPrice >= 30) {
                discountPrice = finalPrice * 0.9
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                view.tvFinalPrice.text = df.format(discountPrice).toString() + "€"            }
        }

        //order placed screen
        view.btnPlaceOrder.setOnClickListener {
            val dialog = DialogOrder()
            fragmentManager?.let { it1 -> dialog.show(it1, "dialog") }
            view.tvOrderList.setText(" ")
            view.tvFinalPrice.text = "0.0"
            LocalStorage.meals = ArrayList<Meal>()
            LocalStorage.drinks = ArrayList<Drink>()
            LocalStorage.desserts = ArrayList<Dessert>()
        }
        return view
    }
}
