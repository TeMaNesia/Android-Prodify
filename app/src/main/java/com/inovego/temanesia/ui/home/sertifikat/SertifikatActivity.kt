package com.inovego.temanesia.ui.home.sertifikat

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.databinding.ActivitySertifikatBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.detail.DetailFeatureActivity
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.FIREBASE_SERTIFIKASI

class SertifikatActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySertifikatBinding
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySertifikatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        adapter = HomeAdapter { item ->
            Intent(this, DetailFeatureActivity::class.java).also {
                it.putExtra("FeatureItem", item)
                startActivity(it)
            }
        }

        homeViewModel.getListData(FIREBASE_SERTIFIKASI)
        homeViewModel.listData.observe(this) {
            it?.let { data -> adapter.submitList(data) }
        }

        binding.rvSertifikat.apply {
            adapter = this@SertifikatActivity.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }
}