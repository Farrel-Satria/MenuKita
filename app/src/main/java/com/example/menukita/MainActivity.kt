package com.example.menukita

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.model.Menu
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("FIREBASE_TEST", "onCreate terpanggil")

        // 🔥 PAKSA pakai database URL yang BENAR
        val database = FirebaseDatabase.getInstance(
            "https://menukita-feb11-default-rtdb.asia-southeast1.firebasedatabase.app"
        )

        val menuRef = database.getReference("menus")

        val menuId = menuRef.push().key!!
        val menu = Menu(
            id = menuId,
            nama = "Tes Menu",
            harga = 10000,
            deskripsi = "Realtime DB jalan"
        )

        menuRef.child(menuId).setValue(menu)
            .addOnSuccessListener {
                Log.d("FIREBASE_TEST", "DATA MASUK 🔥")
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_TEST", "GAGAL: ${e.message}")
            }
    }
}
