package com.example.menukita.model

data class User(
    val id: String? = null,
    val name: String? = "",
    val email: String? = "",
    val role: String? = "user", // "admin" or "user"
    val profileImageUrl: String? = null
)
