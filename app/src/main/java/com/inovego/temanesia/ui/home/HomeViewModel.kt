package com.inovego.temanesia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.inovego.temanesia.data.model.Dokumen
import com.inovego.temanesia.data.model.ListItem
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

    private val listItem = mutableListOf<ListItem>()
    private val _listItem = MutableLiveData<List<ListItem>?>()
    val listData: LiveData<List<ListItem>?> = _listItem

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
                            listItem.add(setBeasiswaItem(data, createdAt, date, listDokumen))
                        }.addOnFailureListener {
                            listItem.add(setBeasiswaItem(data, createdAt, date, listDokumen))
                        }.addOnCompleteListener {
                            processedData += 1
                            if (processedData == totalData){
                                _listItem.value = listItem
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
    ): ListItem {
        return ListItem(
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
            listDokumen = listDokumen
        )
    }
}