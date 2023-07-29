package com.inovego.temanesia.ui.home.beasiswa

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.databinding.ActivityBeasiswaBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.detail.DetailFeatureActivity
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.FIREBASE_BEASISWA
import com.inovego.temanesia.utils.FIREBASE_USER_MOBILE
import com.inovego.temanesia.utils.PARCELABLE_DATA

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

        binding.shimmerLayout.startShimmer()
        homeViewModel.shimmer.observe(this) {
            if (it == false) {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE

            }
        }
        adapter = HomeAdapter(this) { item ->
            Intent(this, DetailFeatureActivity::class.java).also {
                it.putExtra(PARCELABLE_DATA, item)
                startActivity(it)
            }
        }



        homeViewModel.getUserData(FIREBASE_USER_MOBILE).observe(this) { jurusan ->
            homeViewModel.getListItemByJurusan(FIREBASE_BEASISWA, jurusan)

        }
        homeViewModel.beasiswa.observe(this) {
            it?.let { data ->
                adapter.submitList(data)
            }
        }


        binding.rvBeasiswa.apply {
            adapter = this@BeasiswaActivity.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }


}

