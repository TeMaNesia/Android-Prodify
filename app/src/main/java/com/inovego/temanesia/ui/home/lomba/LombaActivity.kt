package com.inovego.temanesia.ui.home.lomba

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.ActivityLombaBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.detail.DetailFeatureActivity
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.cat

class LombaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLombaBinding
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(Firebase.auth, Firebase.firestore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLombaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        adapter = HomeAdapter { item ->
            Intent(this, DetailFeatureActivity::class.java).also {
                it.putExtra(FeatureItem::class.java.simpleName, item)
                startActivity(it)
            }
        }

        homeViewModel.getListData(FIREBASE_LOMBA)
        homeViewModel.listData.observe(this) {
            it?.let { data -> adapter.submitList(data) }
        }

        binding.rvLomba.apply {
            adapter = this@LombaActivity.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }
}