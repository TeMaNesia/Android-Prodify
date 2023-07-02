package com.inovego.temanesia

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

inline fun <reified T> T.cat(message: Any?) =
    Log.i("CatLog ${T::class.java.simpleName}", message.toString())

fun catLogAuth(it: Task<AuthResult>){
    it.cat("Success ${it.isSuccessful}")
    it.cat("Complete ${it.isComplete}")
    it.cat("Canceled ${ it.isCanceled }")

    it.cat("Result ${ it.result.user }")
    it.cat("Result ${ it.result.additionalUserInfo }")
    it.cat("Result ${ it.result.credential }")

    it.cat(it.exception?.message)
}