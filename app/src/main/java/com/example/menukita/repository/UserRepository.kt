package com.example.menukita.repository

import android.util.Log
import com.example.menukita.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository {

    private val db = FirebaseDatabase.getInstance("https://menukita-feb11-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val userRef = db.getReference("users")
    private val auth = FirebaseAuth.getInstance()

    // REGISTER - simpan user baru ke Firebase
    fun register(name: String, email: String, password: String, role: String, onComplete: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            onComplete(false, "Email tidak boleh kosong")
            return
        }
        if (password.length < 6) {
            onComplete(false, "Password minimal 6 karakter")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onComplete(false, task.exception?.message ?: "Registrasi gagal")
                    return@addOnCompleteListener
                }

                val user = User(
                    id = auth.currentUser?.uid,
                    name = name,
                    email = email,
                    role = role,
                    profileImageUrl = null
                )

                val emailKey = email.replace(".", "_").replace("@", "_at_")
                userRef.child(emailKey).setValue(user).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        onComplete(true, null)
                    } else {
                        onComplete(false, dbTask.exception?.message ?: "Gagal menyimpan profil")
                    }
                }
            }
    }

    // LOGIN - cari user berdasarkan field email (key boleh apa saja: "admin", "user1", dll.)
    fun login(email: String, password: String, onComplete: (User?, String?) -> Unit) {
        Log.d("UserRepo", "Login dengan email: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onComplete(null, task.exception?.message ?: "Login gagal")
                    return@addOnCompleteListener
                }

                userRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val userSnapshot = snapshot.children.first()
                                val user = userSnapshot.getValue(User::class.java)
                                onComplete(user, null)
                            } else {
                                onComplete(null, "Profil user tidak ditemukan")
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            onComplete(null, error.message)
                        }
                    })
            }
    }

    fun getUserByEmail(email: String, onComplete: (User?, String?) -> Unit) {
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        val user = userSnapshot.getValue(User::class.java)
                        onComplete(user, null)
                    } else {
                        onComplete(null, "User tidak ditemukan")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(null, error.message)
                }
            })
    }

    fun updateProfile(oldEmail: String, newUser: User, onComplete: (Boolean, String?) -> Unit) {
        if (oldEmail.isBlank() || newUser.email.isNullOrBlank()) {
            onComplete(false, "Email tidak valid")
            return
        }

        val newEmail = newUser.email
        val oldKey = oldEmail.replace(".", "_").replace("@", "_at_")
        val newKey = newEmail!!.replace(".", "_").replace("@", "_at_")

        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "Session tidak valid, silakan login ulang")
            return
        }

        // Jika email berubah, pastikan email baru belum dipakai
        if (oldEmail != newEmail) {
            currentUser.updateEmail(newEmail)
                .addOnCompleteListener { updateTask ->
                    if (!updateTask.isSuccessful) {
                        onComplete(false, updateTask.exception?.message ?: "Gagal update email (butuh login ulang)")
                        return@addOnCompleteListener
                    }
                    userRef.orderByChild("email").equalTo(newEmail)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    onComplete(false, "Email sudah terdaftar!")
                                } else {
                                    userRef.child(newKey).setValue(newUser).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            userRef.child(oldKey).removeValue()
                                            onComplete(true, null)
                                        } else {
                                            onComplete(false, task.exception?.message ?: "Gagal update")
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                onComplete(false, error.message)
                            }
                        })
                }
        } else {
            userRef.child(oldKey).setValue(newUser).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message ?: "Gagal update")
                }
            }
        }
    }

    fun updatePassword(email: String, newPassword: String, onComplete: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "Session tidak valid, silakan login ulang")
            return
        }
        currentUser.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.exception?.message)
            }
    }
}
