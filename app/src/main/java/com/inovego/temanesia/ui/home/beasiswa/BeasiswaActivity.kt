package com.inovego.temanesia.ui.home.beasiswa

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.databinding.ActivityBeasiswaBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.detail.DetailFeatureActivity
import com.inovego.temanesia.ui.home.HomeAdapter
import com.inovego.temanesia.ui.home.HomeViewModel

class BeasiswaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeasiswaBinding
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        adapter = HomeAdapter { item ->
            Intent(this, DetailFeatureActivity::class.java).also {
                it.putExtra("FeatureItem", item)
                startActivity(it)
            }
        }

        homeViewModel.listBeasiswa.observe(this) {
            it?.let { data -> adapter.submitList(data) }
        }

        binding.rvBeasiswa.apply {
            adapter = this@BeasiswaActivity.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }
}