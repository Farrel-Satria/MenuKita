package com.example.menukita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityProfileBinding
import com.example.menukita.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.result.contract.ActivityResultContracts
import com.example.menukita.util.ThemeManager
import com.example.menukita.util.UiFeedback

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
        binding.tvTotalOrders.text = "12"
        binding.tvMemberSince.text = "2024"
        binding.tvPoints.text = "350"
        binding.tvLoyaltyPoints.text = "350 Points"
        binding.progressLoyalty.progress = 35
        if (binding.tvPoints.text.toString().toIntOrNull() ?: 0 >= 300) {
            binding.fireworksAchievement.launchBursts(2)
            UiFeedback.showToast(this, "Achievement: Silver member aktif", UiFeedback.Type.SUCCESS)
        }

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

        binding.btnManageAddresses.setOnClickListener {
            UiFeedback.showToast(this, "Kelola alamat (coming soon)", UiFeedback.Type.INFO)
        }

        binding.btnPaymentMethods.setOnClickListener {
            UiFeedback.showToast(this, "Metode pembayaran (coming soon)", UiFeedback.Type.INFO)
        }

        binding.btnWishlist.setOnClickListener {
            UiFeedback.showToast(this, "Wishlist (coming soon)", UiFeedback.Type.INFO)
        }

        binding.btnSecurity.setOnClickListener {
            UiFeedback.showToast(this, "Keamanan akun (coming soon)", UiFeedback.Type.INFO)
        }

        binding.switchDarkMode.isChecked = ThemeManager.isDarkMode(this)
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDarkMode(this, isChecked)
            recreate()
        }

        binding.switchSeasonal.isChecked = ThemeManager.isSeasonalTheme(this)
        binding.switchSeasonal.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setSeasonalTheme(this, isChecked)
            recreate()
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
