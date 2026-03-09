package com.example.menukita

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.menukita.adapter.MenuGridAdapter
import com.example.menukita.databinding.ActivityUserDashboardBinding
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.util.CartManager
import androidx.appcompat.widget.SearchView

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var menuAdapter: MenuGridAdapter
    private val repository = MenuRepository()

    private var fullMenuList: List<Menu> = listOf()
    private var currentCategory: String = "Semua"
    private var selectedQuery: String = ""
    private var userName: String = "Pelanggan"
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra("USER_NAME") ?: "Pelanggan"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        binding.tvGreeting.text = "Halo, $userName! 👋"

        setupRecyclerView()
        observeData()
        setupSearch()
        setupCategoryFilter()
        setupCart()

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", "user")
            }
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        updateCartBadge()
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuGridAdapter(emptyList()) { menu ->
            CartManager.add(menu)
            updateCartBadge()
            Toast.makeText(this, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
        }

        binding.rvMenu.apply {
            layoutManager = GridLayoutManager(this@UserDashboardActivity, 2)
            adapter = menuAdapter
        }
    }

    private fun observeData() {
        repository.getMenus(
            onDataChange = { menuList ->
                fullMenuList = menuList
                applyFilters()
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                selectedQuery = query ?: ""; applyFilters(); return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                selectedQuery = newText ?: ""; applyFilters(); return true
            }
        })
    }

    private fun setupCategoryFilter() {
        binding.chipGroupKategori.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                currentCategory = when (checkedIds.first()) {
                    R.id.chipMakanan -> "Makanan"
                    R.id.chipMinuman -> "Minuman"
                    R.id.chipDessert -> "Dessert"
                    else -> "Semua"
                }
                applyFilters()
            }
        }
    }

    private fun setupCart() {
        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
            }
            startActivity(intent)
        }
    }

    private fun updateCartBadge() {
        val totalItems = CartManager.getTotalItems()
        binding.tvCartBadge.text = totalItems.toString()
        binding.tvCartBadge.visibility = if (totalItems > 0) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun applyFilters() {
        var list = fullMenuList
        if (currentCategory != "Semua") list = list.filter { it.kategori == currentCategory }
        if (selectedQuery.isNotEmpty()) list = list.filter {
            (it.nama ?: "").contains(selectedQuery, ignoreCase = true) ||
            (it.deskripsi ?: "").contains(selectedQuery, ignoreCase = true)
        }
        menuAdapter.updateData(list)
        binding.tvEmptyState.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }
}
