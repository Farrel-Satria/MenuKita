package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityEditMenuBinding
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.util.NetworkUtils

class EditMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMenuBinding
    private val repository = MenuRepository()
    private var menuId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        getIntentData()
        setupAction()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getIntentData() {
        menuId = intent.getStringExtra("MENU_ID")
        binding.etNamaMenu.setText(intent.getStringExtra("MENU_NAMA"))
        binding.etHargaMenu.setText(intent.getIntExtra("MENU_HARGA", 0).toString())
        binding.etDeskripsiMenu.setText(intent.getStringExtra("MENU_DESKRIPSI"))
    }

    private fun setupAction() {
        binding.btnUpdate.setOnClickListener {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nama = binding.etNamaMenu.text.toString().trim()
            val hargaStr = binding.etHargaMenu.text.toString().trim()
            val deskripsi = binding.etDeskripsiMenu.text.toString().trim()

            if (nama.isEmpty()) {
                binding.etNamaMenu.error = "Nama menu tidak boleh kosong"
                return@setOnClickListener
            }

            if (hargaStr.isEmpty()) {
                binding.etHargaMenu.error = "Harga wajib diisi"
                return@setOnClickListener
            }

            val harga = hargaStr.toIntOrNull()
            if (harga == null || harga <= 0) {
                binding.etHargaMenu.error = "Harga harus berupa angka positif"
                return@setOnClickListener
            }

            val updatedMenu = Menu(id = menuId, nama = nama, harga = harga, deskripsi = deskripsi)

            binding.btnUpdate.isEnabled = false
            repository.updateMenu(updatedMenu) { success ->
                binding.btnUpdate.isEnabled = true
                if (success) {
                    Toast.makeText(this, "Menu berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal memperbarui menu", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnHapus.setOnClickListener {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Menu")
            .setMessage("Apakah Anda yakin ingin menghapus menu ini?")
            .setPositiveButton("Hapus") { _, _ ->
                menuId?.let { id ->
                    repository.deleteMenu(id) { success ->
                        if (success) {
                            Toast.makeText(this, "Menu dihapus", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
