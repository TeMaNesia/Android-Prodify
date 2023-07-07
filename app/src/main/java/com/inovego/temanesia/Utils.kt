package com.inovego.temanesia

import android.util.Log
import android.util.Patterns
import android.view.View
import com.google.android.material.textfield.TextInputLayout

inline fun <reified T> T.cat(message: Any?) =
    Log.i("CatLog ${T::class.java.simpleName}", message.toString())

fun TextInputLayout.showError(errorText: String? = "Lengkapi Input") {
    this.isErrorEnabled = true
    this.error = errorText
}

fun TextInputLayout.hideError() {
    this.isErrorEnabled = false
    this.error = null
}

fun TextInputLayout.visible() {
    this.visibility = View.VISIBLE
}

fun TextInputLayout.gone() {
    this.visibility = View.GONE
}

fun String.isEmailValid(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}