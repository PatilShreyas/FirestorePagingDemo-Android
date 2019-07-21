package com.spdroid.firestore.paging.demo.java.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
    public String authorName;
    public String message;

    public Post() {
    }

    public Post(String authorName, String message) {
        this.authorName = authorName;
        this.message = message;
    }
}