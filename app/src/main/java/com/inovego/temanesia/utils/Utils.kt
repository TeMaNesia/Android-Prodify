package com.inovego.temanesia.utils

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView


@Suppress("UnusedReceiverParameter")
inline fun <reified T> T.cat(message: Any?) =
    Log.i("CatLog ${T::class.java.simpleName}", message.toString())

fun String.isEmailValid(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun createToast(context: Context, message: String) {
    return Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun ShapeableImageView.loadImageFromUrl(urlPoster: String?) =
    Glide.with(this).load(urlPoster).into(this)

fun ImageView.loadImageFromUrl(urlPoster: String?) =
    Glide.with(this).load(urlPoster).into(this)
