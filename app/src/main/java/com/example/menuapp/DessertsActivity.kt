package com.example.menuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.menuapp.API.*
import com.example.menuapp.extensions.onResult
import com.example.menuapp.models.Currency
import com.example.menuapp.models.Dessert
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_desserts.*

class DessertsActivity : AppCompatActivity() {

    private val dessertService: FoodService = FoodClient.client
    private val currencyService: CurrencyService = CurrencyClient.client
    private lateinit var dessertAdapter: DessertAdapter

    // variables for the filtering system
    private var data: List<Dessert> = listOf()
    private var vegan: Boolean = false
    private var glutenfree: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desserts)

        dessertAdapter = DessertAdapter(data) { model, index ->
            val newFragment = DialogMeal(model)
            newFragment.show(supportFragmentManager, "dialog")

        }

        with(rvDesserts) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@DessertsActivity)
            adapter = dessertAdapter
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        dessertService.getAllDesserts().onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    this.data = it
                    dessertAdapter.updateData(it)
                    tvLoading.visibility = View.INVISIBLE
                    ivLoading.visibility = View.INVISIBLE
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        dessertService.getRatingsByType("drink").onResult(
            onResponse = { _, apiData ->
                apiData?.let {
                    dessertAdapter.updateRating(it)
                }
            },
            onFailure = {
                println("There has been an error. ${it?.message}")
            }
        )

        // Filters
        val filters = resources.getStringArray(R.array.filter_dropdown_desserts)

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
                if (spMeals.selectedItem.toString() == filters[0]) { // preference - veg/gf
                    checkBoxVegan.visibility = View.VISIBLE
                    checkBoxGlutenfree.visibility = View.VISIBLE

                    tvNotice.visibility = View.INVISIBLE
                    radioBtnEuro.visibility = View.INVISIBLE
                    radioBtnKuna.visibility = View.INVISIBLE
                } else if (spMeals.selectedItem.toString() == filters[1]) { // currency
                    tvNotice.visibility = View.VISIBLE
                    radioBtnEuro.visibility = View.VISIBLE
                    radioBtnKuna.visibility = View.VISIBLE

                    checkBoxVegan.visibility = View.INVISIBLE
                    checkBoxGlutenfree.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        checkBoxVegan.setOnCheckedChangeListener { _, isChecked ->
            vegan = isChecked
            updateScreen()
        }

        checkBoxGlutenfree.setOnCheckedChangeListener { _, isChecked ->
            glutenfree = isChecked
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
            dessertAdapter.updateConversionRate(conversionRate)
            updateScreen()
        }
    }

    private fun updateScreen() {
        var toDisplay: List<Dessert> = data

        if (vegan) {
            toDisplay = toDisplay.filter {
                it.preference != null && it.preference.toLowerCase() == "vegan"
            }
        }
        if (glutenfree) {
            toDisplay = toDisplay.filter {
                it.preference != null && it.preference.toLowerCase() == "gluten free"
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

        dessertAdapter.updateData(toDisplay)

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