package com.example.menukita

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menukita.adapter.MenuAdapter
import com.example.menukita.databinding.ActivityMainBinding
import com.example.menukita.repository.MenuRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuAdapter: MenuAdapter
    private val repository = MenuRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeData()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter(emptyList()) { menu ->
            // Mengirim data menu ke EditMenuActivity
            val intent = Intent(this, EditMenuActivity::class.java).apply {
                putExtra("MENU_ID", menu.id)
                putExtra("MENU_NAMA", menu.nama)
                putExtra("MENU_HARGA", menu.harga)
                putExtra("MENU_DESKRIPSI", menu.deskripsi)
            }
            startActivity(intent)
        }
        
        binding.rvMenu.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = menuAdapter
        }
    }

    private fun observeData() {
        repository.getMenus(
            onDataChange = { menuList ->
                menuAdapter.updateData(menuList)
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }
}
