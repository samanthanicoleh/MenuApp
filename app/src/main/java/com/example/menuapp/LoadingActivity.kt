package com.example.menuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.Exception

class LoadingActivity : AppCompatActivity() {

    // Show loading page
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_screen)

        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(4000) // 4s

                    val intent = Intent(baseContext, RegisterActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        background.start()
    }
}
