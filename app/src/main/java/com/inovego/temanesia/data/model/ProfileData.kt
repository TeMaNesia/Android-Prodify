package com.inovego.temanesia.data.model

import java.util.Date

object ProfileData {
    private val date = Date()
    val data = listOf(
        ProfileDummy(
            1,
            "Web Developer",
            "Jaya Tekno",
            "Lorem ipsum dolor sit amet consectetur adipiscing elit Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla, mattis ligula consectetur, ultrices mauris.",
            date
        ),
        ProfileDummy(
            2,
            "Mobile Developer",
            "Baya Tekno",
            "Lorem ipsum dolor sit amet consectetur adipiscing elit Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla, mattis ligula consectetur, ultrices mauris.",
            date
        ),
        ProfileDummy(
            3,
            "AI Developer",
            "Daya Tekno",
            "Lorem ipsum dolor sit amet consectetur adipiscing elit Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla, mattis ligula consectetur, ultrices mauris.",
            date
        )
    )
}