package com.example.menukita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("USER_NAME") ?: "Pengguna"
        val email = intent.getStringExtra("USER_EMAIL") ?: "-"
        val role = intent.getStringExtra("USER_ROLE") ?: "user"

        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvProfileRole.text = if (role == "admin") "Administrator" else "Pelanggan"

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnProfileLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}
