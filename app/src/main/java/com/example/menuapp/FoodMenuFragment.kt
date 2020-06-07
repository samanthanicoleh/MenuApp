package com.example.menuapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_food_menu.*

class FoodMenuFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food_menu, container, false)
        return view
    }

    private fun getSelectedMenuItem() {
        mealsMenuBtn.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, MealsActivity::class.java))
                finish()
            }
        }

        drinksMenuBtn.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, DrinksActivity::class.java))
                finish()
            }
        }

        dessertsMenuBtn.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, DessertsActivity::class.java))
                finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSelectedMenuItem()

        currentUser?.let { user ->
            if (user.isEmailVerified) {
                tvUser.text = user.email
            }
        }
    }
}
