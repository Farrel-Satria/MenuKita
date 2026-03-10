package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityChangePasswordBinding
import com.example.menukita.repository.UserRepository

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val userRepository = UserRepository()
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSavePassword.setOnClickListener {
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirm = binding.etConfirmPassword.text.toString().trim()

            if (newPass.length < 6) {
                binding.etNewPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            }
            if (newPass != confirm) {
                binding.etConfirmPassword.error = "Konfirmasi password tidak sama"
                return@setOnClickListener
            }
            if (userEmail.isBlank()) {
                Toast.makeText(this, "Email user tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSavePassword.isEnabled = false
            userRepository.updatePassword(userEmail, newPass) { success, message ->
                binding.btnSavePassword.isEnabled = true
                if (success) {
                    Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, message ?: "Gagal ubah password", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
