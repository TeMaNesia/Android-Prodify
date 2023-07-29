package com.inovego.temanesia.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

data class UserItem(
    val alamat: String,
    val email: String,
    val pendidikan: String,
    val jurusan: String,
    val nama: String,
    val nik: Long,
    val nimNisn: Long,
    val sekolah: String,
)

data class ProfileDummy(
    val id: Int,
    val title: String,
    val company: String,
    val description: String,
    val date: Date,
)

data class Lamaran(
    val id: String,
    val id_users: String,
    val nama_lamaran: String,
    val status: String,
    val createdAt: Date,
)

@Parcelize
data class FeatureItem(
    val idFeature:String,
    val jenisKegiatan: String,
    val status: String,
    val nama: String,
    val deskripsi: String,
    val ringkasan: String,
    val lokasi: String,
    val penyelenggara: String,
    val penyelenggaraUID: String,
    val penyelenggaraEmail: String,
    val jurusanFeature: List<String>?,
    val urlFeature: String,
    val urlPosterImg: String?,
    val urlPenyelenggaraImg: String?,
    var createdAt: Date,
    var date: Date,
    val listDokumen: List<Dokumen>?,
) : Parcelable

@Parcelize
data class Dokumen(
    val namaFile: String,
    val urlFile: String,
) : Parcelable

