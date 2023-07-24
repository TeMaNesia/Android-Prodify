package com.inovego.temanesia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inovego.temanesia.data.model.BeasiswaItem
import com.inovego.temanesia.data.model.Dokumen
import com.inovego.temanesia.utils.FIREBASE_BEASISWA
import com.inovego.temanesia.utils.FIREBASE_DOKUMEN
import com.inovego.temanesia.utils.cat

class HomeViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _dokumen = MutableLiveData<List<Dokumen>?>()
    private val _listBeasiswa = MutableLiveData<List<BeasiswaItem>?>()
    val listBeasiswa: LiveData<List<BeasiswaItem>?> = _listBeasiswa

    init {
        getListDataBeasiswa()
    }

    private fun getListDataBeasiswa() {
        firebaseFirestore.collection(FIREBASE_BEASISWA)
            .get()
            .addOnSuccessListener { doc ->
                val listData = doc?.map { data ->
                    val createdAt = data["created_at"] as Timestamp
                    val date = data["date"] as Timestamp

                    data.reference.collection(FIREBASE_DOKUMEN)
                        .get()
                        .addOnSuccessListener { dokumen ->
                            val listDokumen = dokumen.map { data ->
                                Dokumen(
                                    namaFile = data["nama_file"].toString(),
                                    urlFile = data["url"].toString()
                                )
                            }
                            _dokumen.value = listDokumen
                        }

                    return@map BeasiswaItem(
                        jenisKegiatan = data["jenis_kegiatan"] as String,
                        status = data["status"] as String,
                        nama = data["nama"] as String,
                        deskripsi = data["deskripsi"] as String,
                        ringkasan = data["ringkasan"] as String,
                        lokasi = data["lokasi"] as String,
                        penyelenggara = data["nama_penyelenggara"] as String,
                        penyelenggaraUID = data["penyelenggara_uid"] as String,
                        penyelenggaraEmail = data["email_penyelenggara"] as String,
                        url = data["url"] as String,
                        urlPoster = data["poster"] as String,
                        createdAt = createdAt.toDate(),
                        date = date.toDate(),
                        listDokumen = _dokumen.value
                    )
                }
                _listBeasiswa.value = listData
            }

            .addOnFailureListener { e -> cat("failure : $e") }
    }
}