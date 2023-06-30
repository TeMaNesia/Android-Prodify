package com.inovego.temanesia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inovego.temanesia.ui.auth.AuthActivity

class InitScreenActivity : AppCompatActivity() {
    private val isLogin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (isLogin) {
            true -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            false -> {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
    }
}