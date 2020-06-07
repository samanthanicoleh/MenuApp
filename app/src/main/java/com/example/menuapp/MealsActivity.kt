package com.example.menuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menuapp.API.*
import com.example.menuapp.extensions.onResult
import com.example.menuapp.models.Currency
import com.example.menuapp.models.Meal
import kotlinx.android.synthetic.main.activity_meals.*


class MealsActivity : AppCompatActivity() {

    private val mealService: FoodService = FoodClient.client
    private val currencyService: CurrencyService = CurrencyClient.client
    private lateinit var mealAdapter: MealAdapter

    // variables for the filtering system
    private var data: List<Meal> = listOf()
    private var vegan: Boolean = false
    private var vegetarian: Boolean = false
    private var glutenfree: Boolean = false

    private var chinese: Boolean = false
    private var japanese: Boolean = false
    private var italian: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meals)


        mealAdapter = MealAdapter(data) { model, index ->
            val newFragment = DialogMeal(model)
            newFragment.show(supportFragmentManager, "dialog")
        }

        with(rvMeals) {
            layoutManager = LinearLayoutManager(this@MealsActivity)
            adapter = mealAdapter
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        mealService.getAllMeals().onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    this.data = it
                    mealAdapter.updateMealData(it)
                    tvLoading.visibility = View.INVISIBLE
                    ivLoading.visibility = View.INVISIBLE
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        mealService.getRatingsByType("meal").onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    mealAdapter.updateRating(it)
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        // Filters
        val filters = resources.getStringArray(R.array.filter_dropdown)

        if (spMeals != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filters)
            spMeals.adapter = adapter
        }

        spMeals.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (spMeals.selectedItem.toString() == filters[0]) { // type - chinese/japanese
                    checkBoxChinese.visibility = View.VISIBLE
                    checkBoxJapanese.visibility = View.VISIBLE
                    checkBoxItalian.visibility = View.VISIBLE

                    checkBoxVegan.visibility = View.INVISIBLE
                    checkBoxVegetarian.visibility = View.INVISIBLE
                    checkBoxGlutenfree.visibility = View.INVISIBLE

                    tvNotice.visibility = View.INVISIBLE
                    radioBtnEuro.visibility = View.INVISIBLE
                    radioBtnKuna.visibility = View.INVISIBLE
                } else if (spMeals.selectedItem.toString() == filters[1]) { // preference - veg, gf
                    checkBoxChinese.visibility = View.INVISIBLE
                    checkBoxJapanese.visibility = View.INVISIBLE
                    checkBoxItalian.visibility = View.INVISIBLE

                    tvNotice.visibility = View.INVISIBLE
                    radioBtnEuro.visibility = View.INVISIBLE
                    radioBtnKuna.visibility = View.INVISIBLE

                    checkBoxVegan.visibility = View.VISIBLE
                    checkBoxVegetarian.visibility = View.VISIBLE
                    checkBoxGlutenfree.visibility = View.VISIBLE
                } else if (spMeals.selectedItem.toString() == filters[2]) { // currency
                    checkBoxChinese.visibility = View.INVISIBLE
                    checkBoxJapanese.visibility = View.INVISIBLE
                    checkBoxItalian.visibility = View.INVISIBLE

                    checkBoxVegan.visibility = View.INVISIBLE
                    checkBoxVegetarian.visibility = View.INVISIBLE
                    checkBoxGlutenfree.visibility = View.INVISIBLE

                    tvNotice.visibility = View.VISIBLE
                    radioBtnEuro.visibility = View.VISIBLE
                    radioBtnKuna.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // preference
        checkBoxVegan.setOnCheckedChangeListener { _, isChecked ->
            vegan = isChecked
            updateScreen()
        }

        checkBoxVegetarian.setOnCheckedChangeListener { _, isChecked ->
            vegetarian = isChecked
            updateScreen()
        }

        checkBoxGlutenfree.setOnCheckedChangeListener { _, isChecked ->
            glutenfree = isChecked
            updateScreen()
        }

        // type
        checkBoxChinese.setOnCheckedChangeListener { _, isChecked ->
            chinese = isChecked
            updateScreen()
        }
        checkBoxJapanese.setOnCheckedChangeListener { _, isChecked ->
            japanese = isChecked
            updateScreen()
        }
        checkBoxItalian.setOnCheckedChangeListener { _, isChecked ->
            italian = isChecked
            updateScreen()
        }

        // Fallback in case the api isn't working
        val fallbackRate = 7.56
        var currency = Currency(hashMapOf("HRK" to fallbackRate), "HRK", "2020-04-28")

        currencyService.getKunaConversion("HRK").onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    currency = it
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        radioBtnEuro.setOnCheckedChangeListener { _, isChecked ->
            var conversionRate = 1.0
            if (isChecked) {
                conversionRate = conversionRate
            } else {
                conversionRate = currency.rates["HRK"] ?: fallbackRate
            }
            mealAdapter.updateConversionRate(conversionRate)
            updateScreen()
        }

    }

    private fun updateScreen() {
        var toDisplay: List<Meal> = data

        // preference
        if (vegan) {
            toDisplay = toDisplay.filter {
                it.preference != null && it.preference.toLowerCase() == "vegan"
            }
        }
        if (vegetarian) {
            toDisplay = toDisplay.filter {
                it.preference != null && it.preference.toLowerCase() == "vegetarian"
            }
        }
        if (glutenfree) {
            toDisplay = toDisplay.filter {
                it.preference != null && it.preference.toLowerCase() == "gluten free"
            }
        }

        // type
        if (chinese) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "chinese"
            }
        }
        if (japanese) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "japanese"
            }
        }
        if (italian) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "italian"
            }
        }

        if (toDisplay.isEmpty()) {
            tvNoresults.visibility = View.VISIBLE
            ivNoresults.visibility = View.VISIBLE
            tvContactUs.visibility = View.VISIBLE
        } else {
            tvNoresults.visibility = View.INVISIBLE
            ivNoresults.visibility = View.INVISIBLE
            tvContactUs.visibility = View.INVISIBLE
        }
        mealAdapter.updateMealData(toDisplay)
    }
}