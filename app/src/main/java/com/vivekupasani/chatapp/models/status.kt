package com.vivekupasani.chatapp.models


data class status(
    val userName: String = "",
    val profilePicURL: String = "",
    val userId: String = "",
    val status: String = "",
    val lastUpdated : Long = 0L
)
