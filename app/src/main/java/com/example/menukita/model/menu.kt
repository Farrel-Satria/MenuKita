package com.example.menukita.model

/**
 * Model data untuk Menu Restoran yang lebih lengkap.
 */
data class Menu(
    val id: String? = null,
    val nama: String? = null,
    val harga: Int? = null,
    val deskripsi: String? = null,
    val kategori: String? = "Lainnya",
    val imageUrl: String? = null,
    val isBestSeller: Boolean? = false // Tandai sebagai menu andalan
)
