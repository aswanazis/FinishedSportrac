package com.example.running.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.running.R
import com.example.running.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        gotoMainActivity()
    }

    private fun gotoMainActivity() {
        lifecycleScope.launch {
            delay(2000)
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}