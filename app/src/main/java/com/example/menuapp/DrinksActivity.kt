package com.example.menuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menuapp.API.*
import com.example.menuapp.extensions.onResult
import com.example.menuapp.models.Currency
import com.example.menuapp.models.Drink
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_drinks.*

class DrinksActivity : AppCompatActivity() {

    private val drinkService: FoodService = FoodClient.client
    private val currencyService: CurrencyService = CurrencyClient.client
    private lateinit var drinkAdapter: DrinkAdapter

    // variables for the filtering system
    private var data: List<Drink> = listOf()
    private var wine: Boolean = false
    private var beer: Boolean = false
    private var fizzy: Boolean = false
    private var soft: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drinks)

        drinkAdapter = DrinkAdapter(data) { model, index ->
            val newFragment = DialogMeal(model)
            newFragment.show(supportFragmentManager, "dialog")

        }

        with(rvDrinks) {
            layoutManager = LinearLayoutManager(this@DrinksActivity)
            adapter = drinkAdapter
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        drinkService.getAllDrinks().onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    this.data = it
                    drinkAdapter.updateData(it)
                    tvLoading.visibility = View.INVISIBLE
                    ivLoading.visibility = View.INVISIBLE
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        drinkService.getRatingsByType("drink").onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    drinkAdapter.updateRating(it)
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        // Filters
        val filters = resources.getStringArray(R.array.filter_dropdown_drinks)

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
                if (spMeals.selectedItem.toString() == filters[0]) { // type - wine/beer/soft/fizzy
                    checkBoxWine.visibility = View.VISIBLE
                    checkBoxBeer.visibility = View.VISIBLE
                    checkBoxFizzy.visibility = View.VISIBLE
                    checkBoxSoft.visibility = View.VISIBLE

                    tvNotice.visibility = View.INVISIBLE
                    radioBtnEuro.visibility = View.INVISIBLE
                    radioBtnKuna.visibility = View.INVISIBLE
                } else if (spMeals.selectedItem.toString() == filters[1]) { // currency
                    tvNotice.visibility = View.VISIBLE
                    radioBtnEuro.visibility = View.VISIBLE
                    radioBtnKuna.visibility = View.VISIBLE

                    checkBoxWine.visibility = View.INVISIBLE
                    checkBoxBeer.visibility = View.INVISIBLE
                    checkBoxFizzy.visibility = View.INVISIBLE
                    checkBoxSoft.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        checkBoxWine.setOnCheckedChangeListener { _, isChecked ->
            wine = isChecked
            updateScreen()
        }
        checkBoxBeer.setOnCheckedChangeListener { _, isChecked ->
            beer = isChecked
            updateScreen()
        }
        checkBoxFizzy.setOnCheckedChangeListener { _, isChecked ->
            fizzy = isChecked
            updateScreen()
        }
        checkBoxSoft.setOnCheckedChangeListener { _, isChecked ->
            soft = isChecked
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
            drinkAdapter.updateConversionRate(conversionRate)
            updateScreen()
        }
    }

    private fun updateScreen() {
        var toDisplay: List<Drink> = data

        if (wine) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "wine"
            }
        }
        if (beer) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "beer"
            }
        }
        if (fizzy) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "fizzy"
            }
        }
        if (soft) {
            toDisplay = toDisplay.filter {
                it.type != null && it.type.toLowerCase() == "soft"
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


        drinkAdapter.updateData(toDisplay)
    }

    //RIGHT CORNER MENU
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.signOut_action) {
            FirebaseAuth.getInstance().signOut()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        return true
    }
}


