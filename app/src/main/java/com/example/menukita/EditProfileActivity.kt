package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityEditProfileBinding
import com.example.menukita.model.User
import com.example.menukita.repository.UserRepository

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val userRepository = UserRepository()
    private var oldEmail: String = ""
    private var userRole: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("USER_NAME") ?: ""
        oldEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        userRole = intent.getStringExtra("USER_ROLE") ?: "user"

        binding.etName.setText(name)
        binding.etEmail.setText(oldEmail)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSave.setOnClickListener {
            val newName = binding.etName.text.toString().trim()
            val newEmail = binding.etEmail.text.toString().trim()

            if (newName.isEmpty()) {
                binding.etName.error = "Nama wajib diisi"
                return@setOnClickListener
            }
            if (newEmail.isEmpty()) {
                binding.etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            binding.btnSave.isEnabled = false
            userRepository.getUserByEmail(oldEmail) { existing, _ ->
                val user = User(
                    id = existing?.id,
                    name = newName,
                    email = newEmail,
                    role = userRole,
                    profileImageUrl = existing?.profileImageUrl
                )

                userRepository.updateProfile(oldEmail, user) { success, message ->
                    binding.btnSave.isEnabled = true
                    if (success) {
                        val data = android.content.Intent().apply {
                            putExtra("UPDATED_NAME", newName)
                            putExtra("UPDATED_EMAIL", newEmail)
                        }
                        setResult(RESULT_OK, data)
                        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, message ?: "Gagal update profil", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
