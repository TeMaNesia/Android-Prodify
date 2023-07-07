package com.inovego.temanesia.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inovego.temanesia.data.model.ProfileData
import com.inovego.temanesia.data.model.ProfileDummy

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text

    private val _dummyData = MutableLiveData(ProfileData.data)
    fun getData(): LiveData<List<ProfileDummy>> {
        return _dummyData
    }
}