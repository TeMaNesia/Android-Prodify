package com.inovego.temanesia.ui.discuss

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiscussViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is discuss Fragment"
    }
    val text: LiveData<String> = _text
}