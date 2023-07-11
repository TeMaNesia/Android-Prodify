package com.inovego.temanesia.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.ui.auth.AuthViewModel
import com.inovego.temanesia.ui.home.HomeViewModel

class ViewModelFactory private constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDB: FirebaseFirestore,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(firebaseAuth, firebaseDB) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(firebaseAuth, firebaseDB) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(auth: FirebaseAuth, DB: FirebaseFirestore): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(auth, DB)
            }.also { instance = it }
    }
}