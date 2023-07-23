package com.inovego.temanesia.data.diskus

import com.google.firebase.Timestamp

data class Diskus(
    var tag: MutableList<String>,
    var title: String,
    var content: String,
    var created_at: Timestamp?,
    var uid: String,
    var author_name: String,
    var author_img_url: String,
    var up_vote: MutableList<String>,
    var down_vote: MutableList<String>,
) {
    constructor() : this(mutableListOf<String>(), "", "", null, "","", "", mutableListOf<String>(), mutableListOf<String>())
}
