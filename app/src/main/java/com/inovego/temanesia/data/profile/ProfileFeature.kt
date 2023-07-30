package com.inovego.temanesia.data.profile

data class ProfileFeature(
    var title: String,
    var sub_title: String,
    var description: String,
    var date: String,
){
    constructor() : this("", "", "","")
}