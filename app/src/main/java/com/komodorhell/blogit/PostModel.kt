package com.komodorhell.blogit

import java.sql.Time
import java.sql.Timestamp

data class PostModel(
    val key: String,
    val title: String,
    val description: String,
    val image: String,
    val userId: String,
    val userImage: String,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", "", "", System.currentTimeMillis())
}