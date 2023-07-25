package com.inovego.temanesia.ui.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.data.model.ListItem
import com.inovego.temanesia.databinding.ActivityDetailFeatureBinding
import com.inovego.temanesia.helper.ViewModelFactory
import com.inovego.temanesia.ui.adapter.DetailAdapter
import com.inovego.temanesia.ui.adapter.HomeAdapter
import com.inovego.temanesia.ui.home.HomeViewModel
import com.inovego.temanesia.utils.PARCELABLE_DATA
import com.inovego.temanesia.utils.cat
import com.inovego.temanesia.utils.loadImageFromUrl

class DetailFeatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFeatureBinding
    private lateinit var adapter: DetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listItem = intent?.extras?.getParcelable("FeatureItem") as ListItem?
        binding.apply {
            if (listItem != null) {
                adapter = DetailAdapter {}

                ivPoster.loadImageFromUrl(listItem.urlPoster)
                tvPillFeatures.text = listItem.jenisKegiatan
                tvPillLembaga.text = listItem.penyelenggara
                tvFeaturesTitle.text = listItem.nama
                tvFeaturesDescriptionSingkat.text = listItem.ringkasan
                tvAlamat.text = listItem.lokasi
                tvTanggal.text = listItem.date.toString()

                ivPenyelanggara.loadImageFromUrl(listItem.url)
                tvPenyelenggaraTitle.text = listItem.penyelenggara
                tvPenyelenggaraEmail.text = listItem.penyelenggaraEmail
                cat(listItem.listDokumen)
                adapter.submitList(listItem.listDokumen)
                binding.rvDokumen.apply {
                    adapter = this@DetailFeatureActivity.adapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
        }

    }
}