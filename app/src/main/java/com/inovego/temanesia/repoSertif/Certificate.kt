package com.inovego.temanesia.repoSertif

import com.google.firebase.Timestamp

data class Certificate(
    var nama: String,
    var file_sertifikat: String,
    var nama_lomba: String,
    var nomor: String,
){
    constructor() : this("", "", "","")
}
