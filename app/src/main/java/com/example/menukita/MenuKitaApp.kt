package com.example.menukita

import android.app.Application
import com.example.menukita.util.ThemeManager
import com.google.firebase.database.FirebaseDatabase

class MenuKitaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
