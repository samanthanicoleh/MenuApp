package com.example.menuapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var langBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale() //translation
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        //translation
        val actionBar = supportActionBar
        actionBar?.title = resources.getString(R.string.app_name)

        btnSignUp.setOnClickListener {
            signUp()
        }

        val signUpBtn = btnSignUpBtn
        signUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val signInBtn = btnSignInBtn
        signInBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val noLoginBtn = btnNoLogin
        btnNoLogin.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        //translation
        langBtn = btnLang
        langBtn.setOnClickListener {
            showChangeLang()
        }
    }

    //translation
    private fun showChangeLang() {
        val listLangs = arrayOf("Croatian", "English")
        val builder = AlertDialog.Builder(this@RegisterActivity)
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

    fun signUp() {

      /*  if (etFullName.text.toString().isEmpty()) {
            etFullName.error = "Please enter your full name"
            etFullName.requestFocus()
            return
        }

        if (!Patterns.DOMAIN_NAME.matcher(etFullName.text.toString()).matches()) {
            etFullName.error = "Please enter valid full name"
            etFullName.requestFocus()
            return
        }*/

        if (etEmail.text.toString().isEmpty()) {
            etEmail.error = "Please enter your email."
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = "Please enter valid email."
            etEmail.requestFocus()
            return
        }

        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Please enter yout password."
            etPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }
                } else {
                    // Sign in fails, display a message
                    Toast.makeText(
                        baseContext,
                        "Sign Up failed, user already exists.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}