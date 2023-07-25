package com.inovego.temanesia.ui.home.lowongan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.databinding.ActivityLombaBinding
import com.inovego.temanesia.databinding.ActivityLowonganBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.detail.DetailFeatureActivity
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN

class LowonganActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLowonganBinding
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLowonganBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        adapter = HomeAdapter { item ->
            Intent(this, DetailFeatureActivity::class.java).also {
                it.putExtra("FeatureItem", item)
                startActivity(it)
            }
        }

        homeViewModel.getListData(FIREBASE_LOWONGAN)
        homeViewModel.listData.observe(this) {
            it?.let { data -> adapter.submitList(data) }
        }

        binding.rvLowongan.apply {
            adapter = this@LowonganActivity.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }
}