package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityAddMenuBinding
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.util.NetworkUtils
import android.net.Uri
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private val repository = MenuRepository()

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivPreview.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupKategoriDropdown()
        setupImagePicker()
        setupAction()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupKategoriDropdown() {
        val kategoriList = listOf("Makanan", "Minuman", "Dessert")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            kategoriList
        )

        binding.etKategori.setAdapter(adapter)
    }

    private fun setupImagePicker() {
        binding.btnPilihGambar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun setupAction() {
        binding.btnSimpan.setOnClickListener {

            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Tidak ada koneksi internet!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nama = binding.etNamaMenu.text.toString().trim()
            val hargaStr = binding.etHargaMenu.text.toString().trim()
            val deskripsi = binding.etDeskripsiMenu.text.toString().trim()
            val kategori = binding.etKategori.text.toString().trim()

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

            if (kategori.isEmpty()) {
                binding.etKategori.error = "Kategori wajib dipilih"
                return@setOnClickListener
            }

            val imageUriString = selectedImageUri?.toString()

            val newMenu = Menu(
                nama = nama,
                harga = harga,
                deskripsi = deskripsi,
                kategori = kategori,
                imageUrl = imageUriString
            )

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