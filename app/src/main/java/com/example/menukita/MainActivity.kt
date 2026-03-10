package com.example.menukita

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.menukita.adapter.BannerAdapter
import com.example.menukita.adapter.MenuAdapter
import com.example.menukita.databinding.ActivityMainBinding
import com.example.menukita.model.BannerItem
import com.example.menukita.repository.MenuRepository
import com.example.menukita.model.Menu
import com.google.android.material.tabs.TabLayoutMediator
import com.example.menukita.util.ThemeManager
import com.example.menukita.util.CategoryPrefs
import com.example.menukita.util.FavoriteManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.graphics.drawable.TransitionDrawable
import com.google.android.material.chip.Chip
import com.example.menukita.ui.AmbientParticlesView

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
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var searchAdapter: ArrayAdapter<String>? = null
    private var firstLoad = true
    private var fabExpanded = false
    private var minPrice: Int = 0
    private var maxPrice: Int = Int.MAX_VALUE
    private var onlyFavorites: Boolean = false
    private var currentHeaderRes: Int = R.drawable.bg_gradient_hero_a
    private var displayedMenuList: List<Menu> = listOf()

    private val effectsHandler = Handler(Looper.getMainLooper())
    private val seasonalModeRunner = object : Runnable {
        private var rain = false
        override fun run() {
            if (!ThemeManager.isSeasonalTheme(this@MainActivity)) return
            rain = !rain
            binding.ambientParticles.setMode(if (rain) AmbientParticlesView.Mode.RAIN else AmbientParticlesView.Mode.SNOW)
            effectsHandler.postDelayed(this, 5200)
        }
    }

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
        setupBanner()
        setupRefresh()
        applyHeaderTheme()
        setupFilter()
        attachSwipeToFavorite()
        setupAdvancedFeatures()
        setupLogout() // Logout tetap ada
        setupProfile() // Tambah fungsi Profil
        setupOrders()

        setupQuickFab()
        setupAmbientEffects()
    }

    override fun onResume() {
        super.onResume()
        startBannerAutoScroll()
        setupAmbientEffects()
        applyHeaderTheme()
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacksAndMessages(null)
        effectsHandler.removeCallbacksAndMessages(null)
    }

    private fun setupProfile() {
        binding.tvWelcomeUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", userRole)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }
    }

    private fun setupUIBasedOnRole() {
        if (userRole == "admin") {
            binding.fabAdd.visibility = android.view.View.VISIBLE
            binding.btnOrders.visibility = android.view.View.VISIBLE
            binding.tvWelcomeUser.text = "Halo, $userName! 👋"
        } else {
            binding.fabAdd.visibility = android.view.View.GONE
            binding.btnOrders.visibility = android.view.View.GONE
            binding.tvWelcomeUser.text = "Halo, $userName! 👋"
        }
        binding.fabQuickAdd.visibility = android.view.View.GONE
        binding.fabQuickOrders.visibility = android.view.View.GONE
    }

    private fun setupLogout() {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", userRole)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }
    }

    private fun setupAdvancedFeatures() {
        binding.btnStats.setOnClickListener {
            emitInteractiveParticles(it, ContextCompat.getColor(this, R.color.primary_orange))
            val isVisible = binding.cardStats.visibility == android.view.View.VISIBLE
            binding.cardStats.visibility = if (isVisible) android.view.View.GONE else android.view.View.VISIBLE
        }

        binding.btnSort.setOnClickListener {
            emitInteractiveParticles(it, ContextCompat.getColor(this, R.color.primary_orange))
            showSortDialog()
        }
    }

    private fun setupOrders() {
        binding.btnOrders.setOnClickListener {
            emitInteractiveParticles(it, ContextCompat.getColor(this, R.color.primary_orange))
            if (userRole == "admin") {
                val intent = Intent(this, AdminOrdersActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
            } else {
                Toast.makeText(this, "Hanya admin yang bisa melihat pesanan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupQuickFab() {
        binding.fabAdd.setOnClickListener {
            binding.fabAdd.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            toggleQuickFab()
        }

        binding.fabQuickAdd.setOnClickListener {
            binding.fabQuickAdd.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }

        binding.fabQuickOrders.setOnClickListener {
            binding.fabQuickOrders.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            val intent = Intent(this, AdminOrdersActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }
    }

    private fun toggleQuickFab() {
        fabExpanded = !fabExpanded
        if (fabExpanded) {
            binding.fabAdd.setIconResource(android.R.drawable.ic_menu_close_clear_cancel)
            binding.fabAdd.animate().rotation(135f).setDuration(180).start()
            showFab(binding.fabQuickAdd, 0)
            showFab(binding.fabQuickOrders, 60)
        } else {
            binding.fabAdd.setIconResource(android.R.drawable.ic_input_add)
            binding.fabAdd.animate().rotation(0f).setDuration(180).start()
            hideFab(binding.fabQuickOrders, 0)
            hideFab(binding.fabQuickAdd, 60)
        }
    }

    private fun showFab(view: android.view.View, delay: Long) {
        view.visibility = android.view.View.VISIBLE
        view.alpha = 0f
        view.translationY = 20f
        view.animate().alpha(1f).translationY(0f).setDuration(180).setStartDelay(delay).start()
    }

    private fun hideFab(view: android.view.View, delay: Long) {
        view.animate().alpha(0f).translationY(20f).setDuration(160).setStartDelay(delay)
            .withEndAction { view.visibility = android.view.View.GONE }
            .start()
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

    private fun setupAmbientEffects() {
        val seasonal = ThemeManager.isSeasonalTheme(this)
        effectsHandler.removeCallbacksAndMessages(null)
        if (seasonal) {
            binding.ambientParticles.setMode(AmbientParticlesView.Mode.SNOW)
            effectsHandler.postDelayed(seasonalModeRunner, 5200)
        } else {
            binding.ambientParticles.setMode(AmbientParticlesView.Mode.FLOATING)
        }
    }

    private fun emitInteractiveParticles(anchor: android.view.View, color: Int? = null) {
        val burstPos = IntArray(2)
        val anchorPos = IntArray(2)
        binding.particleBurst.getLocationOnScreen(burstPos)
        anchor.getLocationOnScreen(anchorPos)
        val x = anchorPos[0] - burstPos[0] + (anchor.width / 2f)
        val y = anchorPos[1] - burstPos[1] + (anchor.height / 2f)
        binding.particleBurst.emitAt(x, y, color)
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
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
                updateSearchSuggestions(menuList)
                if (firstLoad) {
                    binding.skeletonInclude.root.visibility = View.GONE
                    firstLoad = false
                }
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
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            selectedQuery = text?.toString() ?: ""
            applyFilters()
        }

        binding.etSearch.setOnItemClickListener { _, _, position, _ ->
            val item = searchAdapter?.getItem(position) ?: ""
            binding.etSearch.setText(item, false)
            selectedQuery = item
            applyFilters()
        }
    }

    private fun updateSearchSuggestions(menuList: List<Menu>) {
        val suggestions = menuList.mapNotNull { it.nama }.distinct().sorted()
        if (searchAdapter == null) {
            searchAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
            binding.etSearch.setAdapter(searchAdapter)
        } else {
            searchAdapter?.clear()
            searchAdapter?.addAll(suggestions)
            searchAdapter?.notifyDataSetChanged()
        }
    }

    private fun setupCategoryFilter() {
        val saved = CategoryPrefs.load(this, "admin")
        if (!saved.isNullOrBlank()) {
            currentCategory = saved
            val chipId = when (saved) {
                "Makanan" -> R.id.chipMakanan
                "Minuman" -> R.id.chipMinuman
                "Dessert" -> R.id.chipDessert
                else -> R.id.chipSemua
            }
            binding.chipGroupKategori.check(chipId)
        }
        binding.chipGroupKategori.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds.first()
                currentCategory = when (chipId) {
                    R.id.chipMakanan -> "Makanan"
                    R.id.chipMinuman -> "Minuman"
                    R.id.chipDessert -> "Dessert"
                    else -> "Semua"
                }
                binding.chipGroupKategori.findViewById<Chip>(chipId)?.let { animateChipSelection(it) }
                CategoryPrefs.save(this, "admin", currentCategory)
                applyFilters()
                animateHeaderGradientForCategory()
            }
        }
    }

    private fun animateChipSelection(chip: Chip) {
        chip.animate().cancel()
        chip.animate().scaleX(1.06f).scaleY(1.06f).setDuration(120)
            .withEndAction {
                chip.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
            }
            .start()
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

        // Price range filter
        filteredList = filteredList.filter { menu ->
            val price = menu.harga ?: 0
            price in minPrice..maxPrice
        }

        // Favorites only
        if (onlyFavorites) {
            filteredList = filteredList.filter { FavoriteManager.isFavorite(it.id) }
        }

        displayedMenuList = filteredList
        menuAdapter.updateData(filteredList)
        binding.tvEmptyState.visibility = if (filteredList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.rvMenu.scheduleLayoutAnimation()
        binding.rvMenu.alpha = 0f
        binding.rvMenu.animate().alpha(1f).setDuration(220).start()
    }

    private fun setupBanner() {
        val items = listOf(
            BannerItem("Promo Hari Ini", "Diskon Rp 50.000", "Untuk menu pilihan", R.drawable.placeholder_food),
            BannerItem("Weekend Feast", "Gratis minuman", "Minimal order Rp 80.000", R.drawable.placeholder_food),
            BannerItem("Chef Special", "Menu baru minggu ini", "Coba sekarang!", R.drawable.placeholder_food)
        )
        bannerAdapter = BannerAdapter(items)
        binding.vpBanner.adapter = bannerAdapter
        TabLayoutMediator(binding.tabBanner, binding.vpBanner) { _, _ -> }.attach()
        binding.vpBanner.setPageTransformer { page, position ->
            page.translationX = -position * 40
            val scale = 0.9f + (1 - kotlin.math.abs(position)) * 0.1f
            page.scaleY = scale
        }
    }

    private fun startBannerAutoScroll() {
        bannerHandler.postDelayed(object : Runnable {
            override fun run() {
                if (bannerAdapter.itemCount == 0) return
                val next = (binding.vpBanner.currentItem + 1) % bannerAdapter.itemCount
                binding.vpBanner.setCurrentItem(next, true)
                bannerHandler.postDelayed(this, 4000)
            }
        }, 4000)
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            applyFilters()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun applyHeaderTheme() {
        val seasonal = ThemeManager.isSeasonalTheme(this)
        val backgroundRes = if (seasonal) {
            R.drawable.bg_gradient_seasonal
        } else {
            headerResForCategory()
        }
        binding.appBar.setBackgroundResource(backgroundRes)
        val bg = binding.appBar.background
        if (bg is AnimationDrawable) {
            bg.setEnterFadeDuration(500)
            bg.setExitFadeDuration(500)
            bg.start()
        }
        setupAmbientEffects()
        currentHeaderRes = backgroundRes
    }

    private fun headerResForCategory(): Int {
        return when (currentCategory) {
            "Makanan" -> R.drawable.bg_gradient_hero_a
            "Minuman" -> R.drawable.bg_gradient_hero_b
            "Dessert" -> R.drawable.bg_gradient_hero_c
            else -> R.drawable.bg_gradient_hero_a
        }
    }

    private fun animateHeaderGradientForCategory() {
        if (ThemeManager.isSeasonalTheme(this)) return
        val targetRes = headerResForCategory()
        if (targetRes == currentHeaderRes) return
        val from = ContextCompat.getDrawable(this, currentHeaderRes) ?: return
        val to = ContextCompat.getDrawable(this, targetRes) ?: return
        val transition = TransitionDrawable(arrayOf(from, to))
        binding.appBar.background = transition
        transition.startTransition(350)
        currentHeaderRes = targetRes
    }

    private fun setupFilter() {
        binding.btnFilter.setOnClickListener {
            val dialog = BottomSheetDialog(this, R.style.Theme_MenuKita_BottomSheetDialog)
            val view = layoutInflater.inflate(R.layout.sheet_filters, null)
            dialog.setContentView(view)

            val chipSortGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipSortGroup)
            val chipSortAZ = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipSortAZ)
            val chipSortLow = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipSortLow)
            val chipSortHigh = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipSortHigh)
            val range = view.findViewById<com.google.android.material.slider.RangeSlider>(R.id.priceRange)
            val switchFav = view.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchFavoriteOnly)
            val btnApply = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnApply)
            val btnReset = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnReset)

            when (currentSort) {
                "PriceAsc" -> chipSortGroup.check(chipSortLow.id)
                "PriceDesc" -> chipSortGroup.check(chipSortHigh.id)
                else -> chipSortGroup.check(chipSortAZ.id)
            }

            val maxVal = (fullMenuList.maxOfOrNull { it.harga ?: 0 } ?: 200000).coerceAtLeast(10000)
            range.valueTo = maxVal.toFloat()
            range.values = listOf(minPrice.toFloat(), maxPrice.coerceAtMost(maxVal).toFloat())
            switchFav.isChecked = onlyFavorites

            btnApply.setOnClickListener {
                currentSort = when (chipSortGroup.checkedChipId) {
                    chipSortLow.id -> "PriceAsc"
                    chipSortHigh.id -> "PriceDesc"
                    else -> "A-Z"
                }
                minPrice = range.values.first().toInt()
                maxPrice = range.values.last().toInt()
                onlyFavorites = switchFav.isChecked
                applyFilters()
                dialog.dismiss()
            }

            btnReset.setOnClickListener {
                currentSort = "A-Z"
                minPrice = 0
                maxPrice = Int.MAX_VALUE
                onlyFavorites = false
                applyFilters()
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun attachSwipeToFavorite() {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val menu = displayedMenuList.getOrNull(position) ?: return
                FavoriteManager.toggle(menu)
                menuAdapter.notifyItemChanged(position)
                Snackbar.make(binding.root, "Favorit diperbarui", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        FavoriteManager.toggle(menu)
                        menuAdapter.notifyItemChanged(position)
                    }
                    .show()
            }
        })
        helper.attachToRecyclerView(binding.rvMenu)
    }
}

