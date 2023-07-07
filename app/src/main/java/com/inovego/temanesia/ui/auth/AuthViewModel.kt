package com.inovego.temanesia.ui.auth

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    private val _section = MutableLiveData<String>()
    val section: LiveData<String> = _section

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    private val _isRegistered = MutableLiveData(false)
    val isRegistered: LiveData<Boolean> = _isRegistered

    private val _isLoggedIn = MutableLiveData(false)
    val isSignedIn: LiveData<Boolean> = _isLoggedIn

    private val _isUserDataSaved = MutableLiveData(false)
    val isUserDataSaved: LiveData<Boolean> = _isUserDataSaved

    private val _isFieldError = MutableLiveData(false)
    val isFieldError: LiveData<Boolean> = _isFieldError

    fun setSection(state: String) {
        _section.value = state
    }

    fun setUID(id: String) {
        _uid.value = id
    }

    fun setIsRegistered(bool: Boolean) {
        _isRegistered.value = bool
    }

    fun setIsLoggedIn(bool: Boolean) {
        _isLoggedIn.value = bool
    }

    fun setIsUserDataSaved(bool: Boolean) {
        _isUserDataSaved.value = bool
    }

    fun isFieldError(text: List<Editable?>) {
        text.map {

        }
    }
}