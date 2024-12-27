package com.vivekupasani.chatapp.models

data class Users(

    val userId: String = "",
    val email: String = "",
    val password: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val about: String = "",
    val userName: String? = null,
    val profilePicURL: String? = null,
    val lastMessage: String? = null,
    val friends: List<String> = emptyList(),
    val friendRequests: List<Map<String, Any>> = emptyList(),
    val token : String? = null
)
