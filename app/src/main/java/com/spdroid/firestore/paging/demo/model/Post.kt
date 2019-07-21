package com.spdroid.firestore.paging.demo.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    var authorName: String? = null,
    var message: String? = null
)