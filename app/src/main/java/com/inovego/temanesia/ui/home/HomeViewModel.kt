package com.inovego.temanesia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.inovego.temanesia.data.model.Dokumen
import com.inovego.temanesia.data.model.FeatureItem
import com.inovego.temanesia.utils.FIREBASE_DOKUMEN
import com.inovego.temanesia.utils.FIREBASE_LOMBA
import com.inovego.temanesia.utils.FIREBASE_LOWONGAN
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

    private val userItem = MutableLiveData<String>()

    private val _lomba = MutableLiveData<List<FeatureItem>?>()
    val lomba: LiveData<List<FeatureItem>?> = _lomba

    private val _lowongan = MutableLiveData<List<FeatureItem>?>()
    val lowongan: LiveData<List<FeatureItem>?> = _lowongan

//    private val _lastMinute = MutableLiveData<List<FeatureItem>?>()
//    val lastMinute: LiveData<List<FeatureItem>?> = _lastMinute

    fun getUserData(userCollectionName: String): LiveData<String> {
        firebaseFirestore.collection(userCollectionName).document(firebaseAuth.uid!!)
            .get().addOnSuccessListener { document ->
                val data = document.data
                if (data != null) {
                    val jurusan = data["jurusan"]
                    userItem.value = jurusan.toString()
                }
            }

        return userItem
    }

    fun getListItemByTime(documentCollectionName: String, jurusan: String) {
        firebaseFirestore.collection(documentCollectionName)
            .whereArrayContains("jurusan", jurusan)
            .whereGreaterThanOrEqualTo("date", Timestamp.now())
            .orderBy("date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { document ->
                val totalData = document.size()
                var processedData = 0
                val list = arrayListOf<FeatureItem>()
                for (item in document) {
                    getDokumentList(item) { listDokumen ->
                        val data = setListItem(item, listDokumen)
                        list.add(data)
                        processedData++
                        if (processedData == totalData) {
                            separateValue(documentCollectionName, list)
                        }
                    }
                }
            }.addOnFailureListener { e ->
                cat("Failure ${e.message}")
            }
    }

    fun getListItemByJurusan(
        documentCollectionName: String,
        jurusan: String,
    ) {
        firebaseFirestore.collection(documentCollectionName)
            .whereArrayContains("jurusan", jurusan)
            .get()
            .addOnSuccessListener { document ->
                val totalData = document.size()
                var processedData = 0
                val list = arrayListOf<FeatureItem>()
                for (item in document) {
                    getDokumentList(item) { listDokumen ->
                        val data = setListItem(item, listDokumen)
                        list.add(data)
                        processedData++
                        if (processedData == totalData) {
                            separateValue(documentCollectionName, list)
                        }
                    }
                }
            }.addOnFailureListener {
                cat("Failure getting the data")
            }
    }

    private fun separateValue(collectionName: String, list: List<FeatureItem>) {
        when (collectionName) {
            FIREBASE_LOMBA -> {
                _lomba.value = list
            }

            FIREBASE_LOWONGAN -> {
                _lowongan.value = list
            }
        }
    }

    private fun getDokumentList(
        document: QueryDocumentSnapshot,
        featureItemList: (List<Dokumen>) -> Unit,
    ) {
        document.reference.collection(FIREBASE_DOKUMEN)
            .get()
            .addOnSuccessListener { file ->
                val listFileData: List<Dokumen> = file.map { data ->
                    return@map Dokumen(
                        namaFile = data["nama_file"].toString(),
                        urlFile = data["url"].toString()
                    )
                }
                featureItemList.invoke(listFileData)
            }


    }


    fun getListData(documentCollectionName: String) {
        firebaseFirestore.collection(documentCollectionName)
            .get()
            .addOnSuccessListener { collection ->
                val totalData = collection.size()
                var processedData = 0
                collection.forEach { collectionDoc ->
                    val listDokumen = mutableListOf<Dokumen>()
                    collectionDoc.reference.collection(FIREBASE_DOKUMEN)
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
                            featureItem.add(
                                setListItem(collectionDoc, listDokumen)
                            )
                        }.addOnFailureListener {
                            featureItem.add(
                                setListItem(collectionDoc, listDokumen)
                            )
                        }.addOnCompleteListener {
                            processedData += 1
                            if (processedData == totalData) {
                                _featureItem.value = featureItem
                            }
                        }
                }
            }

            .addOnFailureListener { e -> cat("failure : $e") }
    }

    private fun setListItem(
        data: QueryDocumentSnapshot,
        listDokumen: List<Dokumen>,
    ): FeatureItem {
        @Suppress("UNCHECKED_CAST")
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
            jurusanFeature = data["jurusan"] as List<String>,
            urlFeature = data["url"] as String,
            urlPosterImg = data["poster"] as String,
            urlPenyelenggaraImg = data["foto_penyelenggara"] as String,
            createdAt = (data["created_at"] as Timestamp).toDate(),
            date = (data["date"] as Timestamp).toDate(),
            listDokumen = listDokumen
        )
    }

}