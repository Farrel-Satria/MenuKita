package com.example.menukita

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.GridLayoutManager
import com.example.menukita.adapter.BannerAdapter
import com.example.menukita.adapter.MenuGridAdapter
import com.example.menukita.adapter.MenuGridAdapter.QuickAction
import com.example.menukita.databinding.ActivityUserDashboardBinding
import com.example.menukita.model.BannerItem
import com.example.menukita.model.Menu
import com.example.menukita.repository.MenuRepository
import com.example.menukita.ui.AmbientParticlesView
import com.example.menukita.util.CartManager
import com.example.menukita.util.FavoriteManager
import com.example.menukita.util.UiFeedback
import com.example.menukita.util.ThemeManager
import com.example.menukita.util.CategoryPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.graphics.drawable.TransitionDrawable
import androidx.core.view.ViewCompat
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GravityCompat
import com.example.menukita.util.TutorialPrefs
import com.google.android.material.tabs.TabLayoutMediator
import com.example.menukita.widget.TutorialOverlayView
import com.google.android.material.chip.Chip
import kotlin.math.abs

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var menuAdapter: MenuGridAdapter
    private val repository = MenuRepository()

    private var fullMenuList: List<Menu> = listOf()
    private var currentCategory: String = "Semua"
    private var selectedQuery: String = ""
    private var userName: String = "Pelanggan"
    private var userEmail: String = ""
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var searchAdapter: ArrayAdapter<String>? = null
    private var firstLoad = true
    private var minPrice: Int = 0
    private var maxPrice: Int = Int.MAX_VALUE
    private var onlyFavorites: Boolean = false
    private var currentHeaderRes: Int = R.drawable.bg_gradient_hero_a
    private var displayedMenuList: List<Menu> = listOf()
    private var userSort: String = "A-Z"
    private var tutorialOverlay: TutorialOverlayView? = null
    private val effectsHandler = Handler(Looper.getMainLooper())
    private val seasonalModeRunner = object : Runnable {
        private var rain = false
        override fun run() {
            if (!ThemeManager.isSeasonalTheme(this@UserDashboardActivity)) return
            rain = !rain
            binding.ambientParticles.setMode(if (rain) AmbientParticlesView.Mode.RAIN else AmbientParticlesView.Mode.SNOW)
            effectsHandler.postDelayed(this, 5200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra("USER_NAME") ?: "Pelanggan"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        binding.tvGreeting.text = "Halo, $userName"

        setupRecyclerView()
        observeData()
        setupSearch()
        setupCategoryFilter()
        setupCart()
        setupBanner()
        setupRefresh()
        setupBottomNav()
        setupAmbientEffects()
        applyHeaderTheme()
        setupFilter()
        setupDrawer()
        showTutorialIfNeeded()
        attachSwipeToFavorite()

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
                putExtra("USER_ROLE", "user")
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }

        binding.fabQuickCheckout.setOnClickListener {
            binding.fabQuickCheckout.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }
    }

    override fun onStart() {
        super.onStart()
        updateCartBadge()
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

    private fun setupRecyclerView() {
        menuAdapter = MenuGridAdapter(
            emptyList(),
            onItemClick = { menu, imageView ->
                val transitionName = "menu_image_${menu.id ?: menu.nama}"
                ViewCompat.setTransitionName(imageView, transitionName)
                val intent = Intent(this, MenuDetailActivity::class.java).apply {
                    putExtra("MENU_ID", menu.id)
                    putExtra("MENU_NAMA", menu.nama)
                    putExtra("MENU_HARGA", menu.harga ?: 0)
                    putExtra("MENU_DESKRIPSI", menu.deskripsi)
                    putExtra("MENU_KATEGORI", menu.kategori)
                    putExtra("MENU_IMAGE_URL", menu.imageUrl)
                    putExtra("TRANSITION_NAME", transitionName)
                }
                val options = android.app.ActivityOptions.makeSceneTransitionAnimation(this, imageView, transitionName)
                startActivity(intent, options.toBundle())
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            },
            onToggleFavorite = { menu, anchor ->
                FavoriteManager.toggle(menu)
                UiFeedback.showToast(this, "Favorit diperbarui", UiFeedback.Type.INFO)
                emitInteractiveParticles(anchor, ContextCompat.getColor(this, R.color.gold_accent))
                menuAdapter.notifyDataSetChanged()
            },
            onLongPress = { menu, anchor ->
                emitInteractiveParticles(anchor, ContextCompat.getColor(this, R.color.primary_orange))
                showActionSheet(menu)
            },
            onFlickAction = { menu, action, anchor ->
                anchor.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                when (action) {
                    QuickAction.ADD_TO_CART -> {
                        CartManager.add(menu)
                        updateCartBadge()
                        UiFeedback.showToast(this, "Quick add ke keranjang", UiFeedback.Type.SUCCESS)
                        emitInteractiveParticles(anchor, ContextCompat.getColor(this, R.color.accent_green))
                    }
                    QuickAction.TOGGLE_FAVORITE -> {
                        FavoriteManager.toggle(menu)
                        UiFeedback.showToast(this, "Quick favorite diperbarui", UiFeedback.Type.INFO)
                        emitInteractiveParticles(anchor, ContextCompat.getColor(this, R.color.gold_accent))
                        menuAdapter.notifyDataSetChanged()
                    }
                }
            }
        )

        binding.rvMenu.apply {
            layoutManager = GridLayoutManager(this@UserDashboardActivity, 2)
            adapter = menuAdapter
        }
    }

    private fun attachSwipeToFavorite() {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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

    private fun observeData() {
        repository.getMenus(
            onDataChange = { menuList ->
                fullMenuList = menuList
                applyFilters()
                updateSearchSuggestions(menuList)
                if (firstLoad) {
                    binding.root.findViewById<View>(R.id.skeletonInclude)?.visibility = View.GONE
                    firstLoad = false
                }
            },
            onError = { error ->
                UiFeedback.showToast(this, "Error: $error", UiFeedback.Type.ERROR)
            }
        )
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
        val saved = CategoryPrefs.load(this, "user")
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
                CategoryPrefs.save(this, "user", currentCategory)
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

    private fun setupCart() {
        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra("USER_NAME", userName)
                putExtra("USER_EMAIL", userEmail)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }
    }

    private fun setupDrawer() {
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val header = binding.navigationView.getHeaderView(0)
        header.findViewById<android.widget.TextView>(R.id.tvDrawerName).text = userName
        header.findViewById<android.widget.TextView>(R.id.tvDrawerEmail).text = userEmail

        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_home -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.drawer_orders -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java).apply {
                        putExtra("USER_EMAIL", userEmail)
                    })
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.drawer_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        putExtra("USER_NAME", userName)
                        putExtra("USER_EMAIL", userEmail)
                        putExtra("USER_ROLE", "user")
                    })
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.drawer_settings -> {
                    UiFeedback.showToast(this, "Settings (coming soon)", UiFeedback.Type.INFO)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.drawer_logout -> {
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Keluar")
                        .setMessage("Yakin ingin logout?")
                        .setPositiveButton("Ya") { _, _ ->
                            startActivity(Intent(this, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_home
        binding.bottomNav.removeBadge(R.id.nav_orders)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_orders -> {
                    val intent = Intent(this, OrderHistoryActivity::class.java).apply {
                        putExtra("USER_EMAIL", userEmail)
                    }
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java).apply {
                        putExtra("USER_NAME", userName)
                        putExtra("USER_EMAIL", userEmail)
                        putExtra("USER_ROLE", "user")
                    }
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
                    true
                }
                else -> false
            }
        }

        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val threshold = 100
            private val velocityThreshold = 500
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null || e2 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) &&
                    abs(diffX) > threshold &&
                    abs(velocityX) > velocityThreshold) {
                    if (diffX > 0) {
                        binding.bottomNav.selectedItemId = R.id.nav_home
                    } else {
                        binding.bottomNav.selectedItemId = R.id.nav_orders
                    }
                    return true
                }
                return false
            }
        })
        binding.appBar.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            false
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
        list = when (userSort) {
            "PriceAsc" -> list.sortedBy { it.harga ?: 0 }
            "PriceDesc" -> list.sortedByDescending { it.harga ?: 0 }
            else -> list.sortedBy { it.nama?.lowercase() }
        }
        list = list.filter { menu ->
            val price = menu.harga ?: 0
            price in minPrice..maxPrice
        }
        if (onlyFavorites) list = list.filter { FavoriteManager.isFavorite(it.id) }
        displayedMenuList = list
        menuAdapter.updateData(list)
        binding.tvEmptyState.visibility = if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.rvMenu.scheduleLayoutAnimation()
        binding.rvMenu.alpha = 0f
        binding.rvMenu.animate().alpha(1f).setDuration(220).start()
    }

    private fun setupBanner() {
        val items = listOf(
            BannerItem("Promo Spesial", "Gratis Ongkir", "Minimal order Rp 50.000", R.drawable.placeholder_food),
            BannerItem("Menu Baru", "Taste of the Week", "Coba menu favorit baru", R.drawable.placeholder_food),
            BannerItem("Happy Hour", "Diskon minuman", "Jam 15.00 - 17.00", R.drawable.placeholder_food)
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

            when (userSort) {
                "PriceAsc" -> chipSortGroup.check(chipSortLow.id)
                "PriceDesc" -> chipSortGroup.check(chipSortHigh.id)
                else -> chipSortGroup.check(chipSortAZ.id)
            }

            val maxVal = (fullMenuList.maxOfOrNull { it.harga ?: 0 } ?: 200000).coerceAtLeast(10000)
            range.valueTo = maxVal.toFloat()
            range.values = listOf(minPrice.toFloat(), maxPrice.coerceAtMost(maxVal).toFloat())
            switchFav.isChecked = onlyFavorites

            btnApply.setOnClickListener {
                userSort = when (chipSortGroup.checkedChipId) {
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
                userSort = "A-Z"
                minPrice = 0
                maxPrice = Int.MAX_VALUE
                onlyFavorites = false
                applyFilters()
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun showActionSheet(menu: Menu) {
        val dialog = BottomSheetDialog(this, R.style.Theme_MenuKita_BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.sheet_menu_actions, null)
        dialog.setContentView(view)
        view.findViewById<android.widget.TextView>(R.id.tvMenuTitle).text = menu.nama ?: "Menu"
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnActionAdd)
            .setOnClickListener {
                CartManager.add(menu)
                updateCartBadge()
                UiFeedback.showToast(this, "Ditambahkan ke keranjang", UiFeedback.Type.SUCCESS)
                emitInteractiveParticles(view, ContextCompat.getColor(this, R.color.accent_green))
                dialog.dismiss()
            }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnActionFavorite)
            .setOnClickListener {
                FavoriteManager.toggle(menu)
                UiFeedback.showToast(this, "Favorit diperbarui", UiFeedback.Type.INFO)
                emitInteractiveParticles(view, ContextCompat.getColor(this, R.color.gold_accent))
                menuAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnActionShare)
            .setOnClickListener {
                val shareText = "Coba menu ${menu.nama} di MenuKita!"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(intent, "Bagikan Menu"))
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun showTutorialIfNeeded() {
        if (TutorialPrefs.isDone(this)) return
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Tutorial Singkat")
            .setMessage("Ingin melihat panduan fitur utama?")
            .setPositiveButton("Mulai") { _, _ ->
                binding.root.post { startTutorial() }
            }
            .setNegativeButton("Lewati") { _, _ ->
                TutorialPrefs.setDone(this, true)
            }
            .show()
    }

    private data class TutorialStep(val target: android.view.View, val title: String, val message: String)

    private fun startTutorial() {
        if (tutorialOverlay != null) return
        val steps = listOf(
            TutorialStep(binding.tilSearch, "Cari Menu", "Gunakan search untuk menemukan menu favorit."),
            TutorialStep(binding.btnFilter, "Filter Cepat", "Atur harga, urutan, dan favorit dari sini."),
            TutorialStep(binding.chipGroupKategori, "Kategori", "Pilih kategori untuk filter cepat."),
            TutorialStep(binding.btnCart, "Keranjang", "Semua pesanan tersimpan di sini."),
            TutorialStep(binding.bottomNav, "Navigasi", "Buka riwayat order dan profil dengan cepat.")
        )

        val overlay = TutorialOverlayView(this)
        tutorialOverlay = overlay
        overlay.alpha = 0f
        binding.drawerLayout.addView(overlay)
        overlay.animate().alpha(1f).setDuration(200).start()

        var currentStep = 0

        fun showStep(index: Int) {
            if (index >= steps.size) {
                endTutorial()
                return
            }
            currentStep = index
            val step = steps[index]
            overlay.bind(step.title, step.message, index + 1, steps.size)
            overlay.highlight(step.target)
        }

        overlay.onNext = { showStep(currentStep + 1) }
        overlay.onSkip = { endTutorial() }

        showStep(0)
    }

    private fun endTutorial() {
        tutorialOverlay?.let { overlay ->
            overlay.animate()
                .alpha(0f)
                .setDuration(180)
                .withEndAction {
                    binding.drawerLayout.removeView(overlay)
                    tutorialOverlay = null
                }
                .start()
        }
        TutorialPrefs.setDone(this, true)
    }
}
