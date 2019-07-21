package com.spdroid.firestore.paging.demo.kotlin.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    var authorName: String? = null,
    var message: String? = null
)