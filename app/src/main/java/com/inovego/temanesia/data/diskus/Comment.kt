package com.inovego.temanesia.data.diskus

import com.google.firebase.Timestamp

data class Comment(
    var id: String,
    var content: String,
    var created_at: Timestamp?,
    var uid: String,
    var author_name: String,
    var author_img_url: String,
    var up_vote: Int,
    var down_vote: Int,
){
    constructor() : this("", "", null, "","", "", 0, 0)
}
