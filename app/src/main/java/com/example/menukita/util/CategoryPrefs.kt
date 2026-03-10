package com.example.menukita.util

import android.content.Context

object CategoryPrefs {
    private const val PREFS = "menukita_prefs"
    private const val KEY_ADMIN = "category_admin"
    private const val KEY_USER = "category_user"

    fun save(context: Context, role: String, category: String) {
        val key = if (role == "admin") KEY_ADMIN else KEY_USER
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(key, category)
            .apply()
    }

    fun load(context: Context, role: String): String? {
        val key = if (role == "admin") KEY_ADMIN else KEY_USER
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(key, null)
    }
}
