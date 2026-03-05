package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityAddMenuBinding
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.util.NetworkUtils
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private val repository = MenuRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupKategoriDropdown()
        setupImagePreview()
        setupAction()
    }

    private fun setupImagePreview() {
        binding.etImageUrl.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val url = s.toString().trim()
                if (url.isNotEmpty()) {
                    Glide.with(this@AddMenuActivity)
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(binding.ivPreview)
                } else {
                    binding.ivPreview.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            }
        })
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupKategoriDropdown() {
        val kategoriList = listOf("Makanan", "Minuman", "Dessert")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kategoriList)
        binding.actKategori.setAdapter(adapter)
    }

    private fun setupAction() {
        binding.btnSimpan.setOnClickListener {
            val nama = binding.etNamaMenu.text.toString().trim()
            val hargaStr = binding.etHargaMenu.text.toString().trim()
            val deskripsi = binding.etDeskripsiMenu.text.toString().trim()
            val kategori = binding.actKategori.text.toString().trim()
            val imageUrl = binding.etImageUrl.text.toString().trim()

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
                binding.actKategori.error = "Kategori wajib dipilih"
                return@setOnClickListener
            }

            val newMenu = Menu(
                nama = nama,
                harga = harga,
                deskripsi = deskripsi,
                kategori = kategori,
                imageUrl = if (imageUrl.isEmpty()) null else imageUrl
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
