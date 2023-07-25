package com.inovego.temanesia.data.diskus

import com.google.firebase.Timestamp

data class Diskus(
    var id: String,
    var tag: MutableList<String>,
    var title: String,
    var content: String,
    var created_at: Timestamp?,
    var uid: String,
    var author_name: String,
    var author_img_url: String,
    var up_vote: Int,
    var down_vote: Int,
    var total_comment: Int,
) {
    constructor() : this("", mutableListOf<String>(), "", "", null, "","", "", 0, 0, 0)
}
