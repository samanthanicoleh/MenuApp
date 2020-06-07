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
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etPassword
import kotlinx.android.synthetic.main.activity_login.etEmail
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var langBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale() //translation
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        //translation
        val actionBar = supportActionBar
        actionBar?.title = resources.getString(R.string.app_name)

        btnLogin.setOnClickListener {
            login()
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
        langBtn = btnLangLogin
        langBtn.setOnClickListener {
            showChangeLang()
        }
    }

    //translation
    private fun showChangeLang() {
        val listLangs = arrayOf("Croatian", "English")
        val builder = AlertDialog.Builder(this@LoginActivity)
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

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser //check if user account exists
        updateUI(user)
    }

    private fun login() {
        if (etEmail.text.toString().isEmpty()) {
            etEmail.error = "Please enter your username."
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = "Please enter valid email."
            etEmail.requestFocus()
            return
        }

        if (etPassword.text.toString().isEmpty()) {
            etPassword.error = "Please enter valid password."
            etPassword.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }

            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (user.isEmailVerified) {
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    baseContext, "Please verify your email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(
                baseContext, "Please try to login again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}