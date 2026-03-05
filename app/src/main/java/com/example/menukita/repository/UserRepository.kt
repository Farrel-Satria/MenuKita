package com.example.menukita.repository

import android.util.Log
import com.example.menukita.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository {

    private val db = FirebaseDatabase.getInstance("https://menukita-feb11-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val userRef = db.getReference("users")

    // REGISTER - simpan user baru ke Firebase
    fun register(user: User, onComplete: (Boolean, String?) -> Unit) {
        val email = user.email
        if (email.isNullOrEmpty()) {
            onComplete(false, "Email tidak boleh kosong")
            return
        }

        // Cek apakah email sudah terdaftar (cari by field email, bukan key)
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        onComplete(false, "Email sudah terdaftar!")
                    } else {
                        // Key bisa apa saja, kita pakai email yang sudah dimodifikasi
                        val emailKey = email.replace(".", "_").replace("@", "_at_")
                        Log.d("UserRepo", "Menyimpan user dengan key: $emailKey")

                        userRef.child(emailKey).setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("UserRepo", "Register BERHASIL!")
                                onComplete(true, null)
                            } else {
                                val err = task.exception?.message ?: "Gagal menyimpan"
                                Log.e("UserRepo", "Register GAGAL: $err")
                                onComplete(false, err)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    onComplete(false, error.message)
                }
            })
    }

    // LOGIN - cari user berdasarkan field email (key boleh apa saja: "admin", "user1", dll.)
    fun login(email: String, onComplete: (User?, String?) -> Unit) {
        Log.d("UserRepo", "Login dengan email: $email")

        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("UserRepo", "User ditemukan!")
                        val userSnapshot = snapshot.children.first()
                        val user = userSnapshot.getValue(User::class.java)
                        onComplete(user, null)
                    } else {
                        Log.w("UserRepo", "User tidak ditemukan untuk email: $email")
                        onComplete(null, "Akun tidak ditemukan. Silakan daftar terlebih dahulu.")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserRepo", "Login error: ${error.message}")
                    onComplete(null, error.message)
                }
            })
    }
}
