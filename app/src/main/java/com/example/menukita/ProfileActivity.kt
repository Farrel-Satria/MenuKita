package com.example.menukita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityProfileBinding
import com.example.menukita.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.result.contract.ActivityResultContracts

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val userRepository = UserRepository()
    private var userEmail: String = ""
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val newName = data?.getStringExtra("UPDATED_NAME")
            val newEmail = data?.getStringExtra("UPDATED_EMAIL")
            if (!newName.isNullOrBlank()) binding.tvProfileName.text = newName
            if (!newEmail.isNullOrBlank()) {
                userEmail = newEmail
                binding.tvProfileEmail.text = newEmail
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("USER_NAME") ?: "Pengguna"
        val email = intent.getStringExtra("USER_EMAIL") ?: "-"
        val role = intent.getStringExtra("USER_ROLE") ?: "user"
        userEmail = email

        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvProfileRole.text = if (role == "admin") "Administrator" else "Pelanggan"

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("USER_NAME", binding.tvProfileName.text.toString())
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", role)
            }
            editProfileLauncher.launch(intent)
        }

        binding.btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java).apply {
                putExtra("USER_EMAIL", userEmail)
            }
            startActivity(intent)
        }

        binding.btnOrderHistory.setOnClickListener {
            val intent = android.content.Intent(this, OrderHistoryActivity::class.java).apply {
                putExtra("USER_EMAIL", userEmail)
            }
            startActivity(intent)
        }

        binding.btnProfileLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val authEmail = FirebaseAuth.getInstance().currentUser?.email
        val emailToLoad = authEmail ?: userEmail
        if (emailToLoad.isNotBlank() && emailToLoad != "-") {
            userEmail = emailToLoad
            userRepository.getUserByEmail(emailToLoad) { user, _ ->
                if (user != null) {
                    binding.tvProfileName.text = user.name
                    binding.tvProfileEmail.text = user.email
                    binding.tvProfileRole.text = if (user.role == "admin") "Administrator" else "Pelanggan"
                }
            }
        }
    }
}
