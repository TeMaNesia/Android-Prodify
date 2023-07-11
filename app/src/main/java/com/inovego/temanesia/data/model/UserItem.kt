package com.inovego.temanesia.data.model

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

data class BeasiswaItem(
    val jenisKegiatan: String,
    val status: String,
    val nama: String,
    val deskripsi: String,
    val ringkasan: String,
    val lokasi: String,
    val penyelenggara: String,
    val penyelenggaraUID: String,
    val penyelenggaraEmail: String,
    val url: String,
    val urlPedoman: String?,
    val urlPoster: String,
    var createdAt: Date,
    var date: Date,
)

