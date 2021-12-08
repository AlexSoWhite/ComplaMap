package com.example.complamap.model



data class Comment (
    val commId: String? = null,
    val complaint: Complaint? = null,
    val author: User? = null,
    val comment_text: String? = null,
    val date: String? = null
    )