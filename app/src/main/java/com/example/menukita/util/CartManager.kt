package com.example.menukita.util

import com.example.menukita.model.Menu

data class CartItem(
    val menuId: String?,
    val name: String?,
    val price: Int,
    var qty: Int,
    val imageUrl: String?,
    val category: String?
)

object CartManager {
    private val items = mutableListOf<CartItem>()

    fun add(menu: Menu) {
        val id = menu.id
        val existing = items.firstOrNull { it.menuId == id }
        if (existing != null) {
            existing.qty += 1
        } else {
            items.add(
                CartItem(
                    menuId = id,
                    name = menu.nama,
                    price = menu.harga ?: 0,
                    qty = 1,
                    imageUrl = menu.imageUrl,
                    category = menu.kategori
                )
            )
        }
    }

    fun updateQty(menuId: String?, newQty: Int) {
        val item = items.firstOrNull { it.menuId == menuId } ?: return
        if (newQty <= 0) {
            items.remove(item)
        } else {
            item.qty = newQty
        }
    }

    fun remove(menuId: String?) {
        val item = items.firstOrNull { it.menuId == menuId } ?: return
        items.remove(item)
    }

    fun clear() {
        items.clear()
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getTotalItems(): Int = items.sumOf { it.qty }

    fun getTotalPrice(): Int = items.sumOf { it.price * it.qty }
}
