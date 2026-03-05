package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityEditMenuBinding
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.util.NetworkUtils

import android.net.Uri
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide

class EditMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMenuBinding
    private val repository = MenuRepository()
    private var menuId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupKategoriDropdown()
        getIntentData()
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
                    Glide.with(this@EditMenuActivity)
                        .load(url)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .into(binding.ivPreview)
                } else {
                    binding.ivPreview.setImageResource(R.drawable.placeholder_food)
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

    private fun getIntentData() {
        menuId = intent.getStringExtra("MENU_ID")
        val imageUrl = intent.getStringExtra("MENU_IMAGE_URL")
        
        binding.etNamaMenu.setText(intent.getStringExtra("MENU_NAMA"))
        binding.etHargaMenu.setText(intent.getIntExtra("MENU_HARGA", 0).toString())
        binding.etDeskripsiMenu.setText(intent.getStringExtra("MENU_DESKRIPSI"))
        binding.actKategori.setText(intent.getStringExtra("MENU_KATEGORI"), false)
        binding.etImageUrl.setText(imageUrl)

        // Muat preview awal
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(binding.ivPreview)
        }
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

            val updatedMenu = Menu(
                id = menuId, 
                nama = nama, 
                harga = harga, 
                deskripsi = deskripsi,
                kategori = kategori,
                imageUrl = if (imageUrl.isEmpty()) null else imageUrl
            )

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
