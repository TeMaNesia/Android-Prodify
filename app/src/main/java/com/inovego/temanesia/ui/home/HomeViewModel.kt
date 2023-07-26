package com.inovego.temanesia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.inovego.temanesia.data.model.Dokumen
import com.inovego.temanesia.data.model.FeatureItem
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

    private val featureItem = mutableListOf<FeatureItem>()
    private val _featureItem = MutableLiveData<List<FeatureItem>?>()
    val listData: LiveData<List<FeatureItem>?> = _featureItem

    init {
//        getListDataBeasiswa(FIREBASE_DOKUMEN)
//        getListDataBeasiswa(FIREBASE_BEASISWA)
//        getListDataBeasiswa(FIREBASE_SERTIFIKASI)
//        getListDataBeasiswa(FIREBASE_LOWONGAN)
    }

    fun getListData(collectionName: String) {
        firebaseFirestore.collection(collectionName)
            .get()
            .addOnSuccessListener { doc ->
                val totalData = doc.size()
                var processedData = 0
                doc.forEach { data ->
                    val createdAt = data["created_at"] as Timestamp
                    val date = data["date"] as Timestamp
                    val listDokumen = mutableListOf<Dokumen>()

                    data.reference.collection(FIREBASE_DOKUMEN)
                        .get()
                        .addOnSuccessListener { dokumen ->
                            listDokumen.addAll(
                                dokumen.map { data ->
                                    Dokumen(
                                        namaFile = data["nama_file"].toString(),
                                        urlFile = data["url"].toString()
                                    )
                                }
                            )
                            featureItem.add(setBeasiswaItem(data, createdAt, date, listDokumen))
                        }.addOnFailureListener {
                            featureItem.add(setBeasiswaItem(data, createdAt, date, listDokumen))
                        }.addOnCompleteListener {
                            processedData += 1
                            if (processedData == totalData){
                                _featureItem.value = featureItem
                            }
                        }
                }
            }

            .addOnFailureListener { e -> cat("failure : $e") }
    }

    private fun setBeasiswaItem(
        data: QueryDocumentSnapshot,
        createdAt: Timestamp,
        date: Timestamp,
        listDokumen: List<Dokumen>,
    ): FeatureItem {
        return FeatureItem(
            jenisKegiatan = data["jenis_kegiatan"] as String,
            status = data["status"] as String,
            nama = data["nama"] as String,
            deskripsi = data["deskripsi"] as String,
            ringkasan = data["ringkasan"] as String,
            lokasi = data["lokasi"] as String,
            penyelenggara = data["nama_penyelenggara"] as String,
            penyelenggaraUID = data["penyelenggara_uid"] as String,
            penyelenggaraEmail = data["email_penyelenggara"] as String,
            urlFeature = data["url"] as String,
            urlPosterImg = data["poster"] as String,
            urlPenyelenggaraImg = data["foto_penyelenggara"] as String,
            createdAt = createdAt.toDate(),
            date = date.toDate(),
            listDokumen = listDokumen
        )
    }
}