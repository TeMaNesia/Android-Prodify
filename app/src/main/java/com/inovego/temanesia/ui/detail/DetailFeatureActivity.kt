package com.inovego.temanesia.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.core.view.View
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.databinding.ActivityDetailFeatureBinding
import com.inovego.temanesia.ui.adapter.DetailAdapter
import com.inovego.temanesia.utils.FIREBASE_BEASISWA
import com.inovego.temanesia.utils.FIREBASE_LAMARAN
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
import com.inovego.temanesia.utils.FIREBASE_SERTIFIKASI
import com.inovego.temanesia.utils.createToast
import com.inovego.temanesia.utils.loadImageFromUrl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailFeatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFeatureBinding
    private lateinit var adapter: DetailAdapter
    private val userId = Firebase.auth.uid.toString()

    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val featureItem = IntentCompat.getParcelableExtra(
            intent,
            FeatureItem::class.java.simpleName,
            FeatureItem::class.java
        )


        binding.apply {
            if (featureItem != null) {
                adapter = DetailAdapter {
                    val openUrl = Intent(Intent.ACTION_VIEW)
                    openUrl.data = Uri.parse(it.urlFile)
                    startActivity(openUrl)
                }

                ivPoster.loadImageFromUrl(featureItem.urlPosterImg)
                tvPillFeatures.text = featureItem.jenisKegiatan
                tvPillLembaga.text = featureItem.penyelenggara
                tvFeaturesTitle.text = featureItem.nama
                tvFeaturesDescription.text = featureItem.deskripsi
                tvFeaturesDescriptionSingkat.text = featureItem.ringkasan
                tvAlamat.text = featureItem.lokasi
                tvTanggal.text = formatter.format(featureItem.date).toString()

                ivPenyelanggara.loadImageFromUrl(featureItem.urlPenyelenggaraImg)
                tvPenyelenggaraTitle.text = featureItem.penyelenggara
                tvPenyelenggaraEmail.text = featureItem.penyelenggaraEmail
                adapter.submitList(featureItem.listDokumen)
                rvDokumen.apply {
                    adapter = this@DetailFeatureActivity.adapter
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
                btnDetail.text = "Daftar Sekarang"

                if (featureItem.jenisKegiatan.lowercase() == "lowongan") {
                    btnDetail.setOnClickListener {
                        val users = HashMap<String, Any>()
                        users["nama_lowongan"] = featureItem.nama
                        users["id_lowongan"] = featureItem.idFeature
                        users["id_users"] = userId
                        users["created_at"] = Date()
                        users["status"] = "Dalam Proses"
                        Firebase.firestore.collection(FIREBASE_LAMARAN)
                            .add(users)
                            .addOnSuccessListener {
                                createToast(this@DetailFeatureActivity, "Berhasil Mendaftar")
                                finish()
                            }.addOnFailureListener { e ->
                                createToast(this@DetailFeatureActivity, "Gagal : ${e.message}")
                            }
                    }
                } else {
                    btnDetail.setOnClickListener {
                        val openUrl = Intent(Intent.ACTION_VIEW)
                        openUrl.data = Uri.parse(featureItem.urlFeature)
                        startActivity(openUrl)
                    }
                }

                when(featureItem.jenisKegiatan){
                    "Lomba" ->{
                        tvPillFeatures.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue100))
                        tvFeaturesTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.blue100))
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_blue_bg)
                    }
                    FIREBASE_SERTIFIKASI ->{
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_orange_bg)
                        tvFeaturesTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.orange100))
                        tvPillFeatures.setTextColor(ContextCompat.getColor(applicationContext, R.color.orange100))
                    }
                    FIREBASE_LOWONGAN ->{
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_red_bg)
                        tvFeaturesTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.red100))
                        tvPillFeatures.setTextColor(ContextCompat.getColor(applicationContext, R.color.red100))
                    }
                    FIREBASE_BEASISWA ->{
                        tvPillFeatures.setBackgroundResource(R.drawable.component_pill_green_bg)
                        tvFeaturesTitle.setTextColor(ContextCompat.getColor(applicationContext, R.color.green100))
                        tvPillFeatures.setTextColor(ContextCompat.getColor(applicationContext, R.color.green100))
                    }
                }
            }
        }

    }
}