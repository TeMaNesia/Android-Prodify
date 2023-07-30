package com.inovego.temanesia.repoSertif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.R
import com.inovego.temanesia.data.model.Lamaran
import com.inovego.temanesia.databinding.ActivityCertificateRepositoryBinding
import com.inovego.temanesia.lamaran.LamaranAdapter
import com.inovego.temanesia.utils.createToast

class CertificateRepositoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertificateRepositoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificateRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = Firebase.auth.uid
        Firebase.firestore.collection("sertificate").whereEqualTo("id_peserta", uid).get()
            .addOnSuccessListener { doc ->
                val listSertifikat = doc.map {
                    Certificate(
                        nama = it["nama"] as String,
                        file_sertifikat = it["file_sertifikat"] as String,
                        nama_lomba = it["nama_lomba"] as String,
                        nomor = it["nomor"] as String,
                    )
                } as MutableList

                val adapterSertifikat = CertificateRepositoyAdapter(this)

                adapterSertifikat.setData(listSertifikat)

                binding.rvSertifikat.apply {
                    adapter = adapterSertifikat
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
            .addOnFailureListener {
                createToast(this, it.message.toString())
            }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}