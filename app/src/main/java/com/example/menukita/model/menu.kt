package com.example.menukita.model

/**
 * Model data untuk Menu Restoran.
 * Digunakan untuk mapping data dari/ke Firebase Realtime Database.
 */
data class Menu(
    val id: String? = null,
    val nama: String? = null,
    val harga: Int? = null,
    val deskripsi: String? = null
)
