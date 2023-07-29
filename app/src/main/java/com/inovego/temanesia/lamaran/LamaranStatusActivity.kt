package com.inovego.temanesia.lamaran

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.inovego.temanesia.data.model.Lamaran
import com.inovego.temanesia.databinding.ActivityLamaranStatusBinding
import com.inovego.temanesia.utils.createToast

class LamaranStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLamaranStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLamaranStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = Firebase.auth.uid
        Firebase.firestore.collection("lamaran").whereEqualTo("id_users", uid).get()
            .addOnSuccessListener { doc ->
                val listLamaran = doc.map {
                    Lamaran(
                        id = it["id_lowongan"] as String,
                        id_users = it["id_users"] as String,
                        nama_lamaran = it["nama_lowongan"] as String,
                        status = it["status"] as String,
                        createdAt = (it["created_at"] as Timestamp).toDate()
                    )
                }
                val adapterLamaran = LamaranAdapter()
                adapterLamaran.submitList(listLamaran)
                binding.rvLamaran.apply {
                    adapter = adapterLamaran
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
            }
            .addOnFailureListener {
                createToast(this, it.message.toString())
            }
    }
}