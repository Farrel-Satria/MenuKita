package com.example.menukita.model

data class OrderItem(
    val menuId: String? = null,
    val name: String? = null,
    val price: Int? = null,
    val qty: Int? = null
)

data class Order(
    val id: String? = null,
    val userEmail: String? = null,
    val items: List<OrderItem> = emptyList(),
    val total: Int? = 0,
    val status: String? = "Menunggu",
    val paymentMethod: String? = "Cash",
    val orderType: String? = "Dine In", // Dine In or Take Away
    val notes: String? = "",
    val address: String? = "",
    val createdAt: Long? = null
)
