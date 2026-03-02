package com.example.menukita

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menukita.adapter.MenuAdapter
import com.example.menukita.databinding.ActivityMainBinding
import com.example.menukita.repository.MenuRepository
import com.example.menukita.model.Menu
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuAdapter: MenuAdapter
    private val repository = MenuRepository()

    private var fullMenuList: List<Menu> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeData()
        setupSearch()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter(emptyList()) { menu ->
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
                fullMenuList = menuList
                menuAdapter.updateData(menuList)
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenu(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenu(newText)
                return true
            }
        })
    }

    private fun filterMenu(query: String?) {
        if (query.isNullOrEmpty()) {
            menuAdapter.updateData(fullMenuList)
            return
        }

        val filteredList = fullMenuList.filter { menu ->
            val nama = menu.nama ?: ""
            val deskripsi = menu.deskripsi ?: ""

            nama.contains(query, ignoreCase = true) ||
                    deskripsi.contains(query, ignoreCase = true)
        }

        menuAdapter.updateData(filteredList)
    }
}