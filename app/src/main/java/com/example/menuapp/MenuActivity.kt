package com.example.menuapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.activity_login.*


class MenuActivity : AppCompatActivity() {

    //objects for fragments
    lateinit var foodMenuFragment: FoodMenuFragment
    lateinit var orderFragment: OrderFragment

    lateinit var langBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        loadLocale() //translation

        val chipNavigationBar: ChipNavigationBar = findViewById(R.id.bottomNavBar)

        //set default fragment
        chipNavigationBar.setItemSelected(R.id.menu_action)
        foodMenuFragment = FoodMenuFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, foodMenuFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        //translation
        val actionBar = supportActionBar
        actionBar?.title = resources.getString(R.string.app_name)

        //translation
        langBtn = btnLangLogin
        langBtn.setOnClickListener {
            showChangeLang()
        }

        //bottom navBar
        chipNavigationBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.menu_action -> {
                    foodMenuFragment = FoodMenuFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, foodMenuFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.orders_action -> {
                    orderFragment = OrderFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, orderFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.signOut_action -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("LOGOUT: Are you sure?")
                        setPositiveButton("Yes") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            finish()
                            startActivity(Intent(context, LoginActivity::class.java))
                        }
                        setNegativeButton("Cancel") { _, _ ->
                            chipNavigationBar.setItemSelected(R.id.menu_action)
                            foodMenuFragment = FoodMenuFragment()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, foodMenuFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit()
                        }
                    }.create().show()
                }
            }
        }
    }

    //bottom navBar
    private fun getColors(): IntArray {
        return intArrayOf(
            ContextCompat.getColor(this, R.color.nudeOne),
            ContextCompat.getColor(this, R.color.nudeTwo),
            ContextCompat.getColor(this, R.color.nudeThree)
        )
    }

    //RIGHT CORNER MENU
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu);
        return true
    }

    //translation
    private fun showChangeLang() {
        val listLangs = arrayOf("Croatian", "English")
        val builder = AlertDialog.Builder(this@MenuActivity)
        builder.setTitle("Choose Language")
        builder.setSingleChoiceItems(listLangs, -1) { dialog, which ->
            if (which == 0) {
                setLocate("hr")
                recreate()
            } else if (which == 1) {
                setLocate("en")
                recreate()
            }
            dialog.dismiss()
        }
        val langDialog = builder.create()
        langDialog.show()
    }

    //translation
    private fun setLocate(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val settings = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        settings.putString("My_Lang", Lang)
        settings.apply()
    }

    //translation
    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (language != null) {
            setLocate(language)
        }
    }
}