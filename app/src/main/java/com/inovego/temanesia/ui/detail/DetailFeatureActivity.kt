package com.inovego.temanesia.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inovego.temanesia.data.model.BeasiswaItem
import com.inovego.temanesia.databinding.ActivityDetailFeatureBinding
import com.inovego.temanesia.utils.cat
import com.inovego.temanesia.utils.loadImageFromUrl

class DetailFeatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFeatureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent?.extras?.getParcelable("FeatureItem") as BeasiswaItem?
        binding.apply {
            if (item != null) {
                ivPoster.loadImageFromUrl(item.urlPoster)
                tvPillFeatures.text = item.jenisKegiatan
                tvPillLembaga.text = item.penyelenggara
                tvFeaturesTitle.text = item.nama
                tvFeaturesDescriptionSingkat.text = item.ringkasan
                tvAlamat.text = item.lokasi
                tvTanggal.text = item.date.toString()

                ivPenyelanggara.loadImageFromUrl(item.url)
                tvPenyelenggaraTitle.text = item.penyelenggara
                tvPenyelenggaraEmail.text = item.penyelenggaraEmail
            }
        }
    }
}