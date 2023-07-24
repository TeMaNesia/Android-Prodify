package com.inovego.temanesia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.ui.auth.AuthActivity
import com.inovego.temanesia.utils.createToast

class InitScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Firebase.auth.currentUser != null) {
            if (Firebase.auth.currentUser!!.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                createToast(this, "Mohon untuk verifikasi Email")
                gotoAuth()
            }
//            startActivity(Intent(this, AuthActivity::class.java))
//            createToast(this, "sudah login")
        } else {
            gotoAuth()
        }
    }

    private fun gotoAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}
