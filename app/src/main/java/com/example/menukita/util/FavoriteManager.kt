package com.example.menukita.util

import com.example.menukita.model.Menu

object FavoriteManager {
    private val favorites = mutableSetOf<String>()

    fun toggle(menu: Menu) {
        val id = menu.id ?: return
        if (favorites.contains(id)) {
            favorites.remove(id)
        } else {
            favorites.add(id)
        }
    }

    fun isFavorite(menuId: String?): Boolean {
        return menuId != null && favorites.contains(menuId)
    }
}
