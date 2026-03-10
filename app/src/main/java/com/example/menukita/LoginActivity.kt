package com.example.menukita

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityLoginBinding
import com.example.menukita.repository.UserRepository
import com.example.menukita.model.User
import com.example.menukita.util.OnboardingPrefs

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!OnboardingPrefs.isDone(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoaderAnimation()
        setupUI()
    }

    private fun startLoaderAnimation() {
        // Animation will be handled by layout drawable
        try {
            val view = binding.waveLoading as View
            val bg = view.background
            if (bg is AnimationDrawable) {
                bg.start()
            }
        } catch (e: Exception) {
            // Handle animation error gracefully
        }
    }

    private fun setupUI() {
        // Tautkan Register di sini (kita akan buat TextView di layout-nya)
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            binding.loadingContainer.visibility = android.view.View.VISIBLE
            userRepository.login(email, password) { user: User?, message: String? ->
                binding.btnLogin.isEnabled = true
                binding.loadingContainer.visibility = android.view.View.GONE
                if (user != null) {
                    Toast.makeText(this, "Berhasil masuk sebagai ${user.role}", Toast.LENGTH_SHORT).show()
                    
                    // Route berdasarkan role
                    if (user.role == "admin") {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("USER_ROLE", user.role)
                            putExtra("USER_NAME", user.name)
                            putExtra("USER_EMAIL", user.email)
                        }
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, UserDashboardActivity::class.java).apply {
                            putExtra("USER_NAME", user.name)
                            putExtra("USER_EMAIL", user.email)
                        }
                        startActivity(intent)
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Login Gagal: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
