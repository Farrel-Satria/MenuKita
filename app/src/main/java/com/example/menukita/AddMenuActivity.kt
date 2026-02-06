package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityAddMenuBinding
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.util.NetworkUtils

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private val repository = MenuRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAction()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupAction() {
        binding.btnSimpan.setOnClickListener {
            // 1. Cek Koneksi Internet
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nama = binding.etNamaMenu.text.toString().trim()
            val hargaStr = binding.etHargaMenu.text.toString().trim()
            val deskripsi = binding.etDeskripsiMenu.text.toString().trim()

            // 2. Validasi Input Lengkap
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

            val newMenu = Menu(nama = nama, harga = harga, deskripsi = deskripsi)

            // Disable button agar tidak double click
            binding.btnSimpan.isEnabled = false
            
            repository.addMenu(newMenu) { success ->
                binding.btnSimpan.isEnabled = true
                if (success) {
                    Toast.makeText(this, "Menu berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menambah menu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
