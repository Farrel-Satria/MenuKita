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
    private var currentCategory: String = "Semua"
    private var selectedQuery: String = ""
    private var currentSort: String = "A-Z"
    private var userRole: String = "user" // Default ke user

    private var userName: String = "Pengguna"
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data User dari Intent
        userRole = intent.getStringExtra("USER_ROLE") ?: "user"
        userName = intent.getStringExtra("USER_NAME") ?: "Pengguna"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        setupUIBasedOnRole()
        setupRecyclerView()
        observeData()
        setupSearch()
        setupCategoryFilter()
        setupAdvancedFeatures()
        setupLogout() // Logout tetap ada
        setupProfile() // Tambah fungsi Profil
        setupOrders()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupProfile() {
        binding.tvWelcomeUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", userRole)
            }
            startActivity(intent)
        }
    }

    private fun setupUIBasedOnRole() {
        if (userRole == "admin") {
            binding.fabAdd.visibility = android.view.View.VISIBLE\n            binding.btnOrders.visibility = android.view.View.VISIBLE\n            binding.tvWelcomeUser.text = "Halo, $userName! 👋"
        } else {
            binding.fabAdd.visibility = android.view.View.GONE\n            binding.btnOrders.visibility = android.view.View.GONE\n            binding.tvWelcomeUser.text = "Halo, $userName! 👋"
        }
    }

    private fun setupLogout() {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", userRole)
            }
            startActivity(intent)
        }
    }

    private fun setupAdvancedFeatures() {
        binding.btnStats.setOnClickListener {
            val isVisible = binding.cardStats.visibility == android.view.View.VISIBLE
            binding.cardStats.visibility = if (isVisible) android.view.View.GONE else android.view.View.VISIBLE
        }

        binding.btnSort.setOnClickListener {
            showSortDialog()
        }
    }

    private fun setupOrders() {
        binding.btnOrders.setOnClickListener {
            if (userRole == "admin") {
                val intent = Intent(this, AdminOrdersActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Hanya admin yang bisa melihat pesanan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("A - Z", "Harga Terendah", "Harga Tertinggi")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Urutkan Berdasarkan")
            .setItems(options) { _, which ->
                currentSort = when (which) {
                    0 -> "A-Z"
                    1 -> "PriceAsc"
                    2 -> "PriceDesc"
                    else -> "A-Z"
                }
                applyFilters()
            }
            .show()
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter(emptyList()) { menu ->
            if (userRole == "admin") {
                val intent = Intent(this, EditMenuActivity::class.java).apply {
                    putExtra("MENU_ID", menu.id)
                    putExtra("MENU_NAMA", menu.nama)
                    putExtra("MENU_HARGA", menu.harga)
                    putExtra("MENU_DESKRIPSI", menu.deskripsi)
                    putExtra("MENU_KATEGORI", menu.kategori)
                    putExtra("MENU_IMAGE_URL", menu.imageUrl)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Hanya admin yang bisa mengubah menu", Toast.LENGTH_SHORT).show()
            }
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
                updateSummary(menuList)
                applyFilters()
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun updateSummary(list: List<Menu>) {
        binding.tvTotalMenu.text = "Total: ${list.size} Menu"
        
        if (list.isNotEmpty()) {
            val minPrice = list.minByOrNull { it.harga ?: 0 }?.harga ?: 0
            val maxPrice = list.maxByOrNull { it.harga ?: 0 }?.harga ?: 0
            
            val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
            binding.tvMinPrice.text = formatter.format(minPrice)
            binding.tvMaxPrice.text = formatter.format(maxPrice)
        } else {
            binding.tvMinPrice.text = "Rp 0"
            binding.tvMaxPrice.text = "Rp 0"
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                selectedQuery = query ?: ""
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                selectedQuery = newText ?: ""
                applyFilters()
                return true
            }
        })
    }

    private fun setupCategoryFilter() {
        binding.chipGroupKategori.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds.first()
                currentCategory = when (chipId) {
                    R.id.chipMakanan -> "Makanan"
                    R.id.chipMinuman -> "Minuman"
                    R.id.chipDessert -> "Dessert"
                    else -> "Semua"
                }
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        var filteredList = fullMenuList

        // Filter by Category
        if (currentCategory != "Semua") {
            filteredList = filteredList.filter { it.kategori == currentCategory }
        }

        // Filter by Search Query
        if (selectedQuery.isNotEmpty()) {
            filteredList = filteredList.filter { menu ->
                val nama = menu.nama ?: ""
                val deskripsi = menu.deskripsi ?: ""
                nama.contains(selectedQuery, ignoreCase = true) ||
                        deskripsi.contains(selectedQuery, ignoreCase = true)
            }
        }

        // Apply Sorting
        filteredList = when (currentSort) {
            "PriceAsc" -> filteredList.sortedBy { it.harga ?: 0 }
            "PriceDesc" -> filteredList.sortedByDescending { it.harga ?: 0 }
            else -> filteredList.sortedBy { it.nama?.lowercase() }
        }

        menuAdapter.updateData(filteredList)
        binding.tvEmptyState.visibility = if (filteredList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }
}

