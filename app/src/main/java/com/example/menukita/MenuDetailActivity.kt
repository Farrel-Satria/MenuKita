package com.example.menukita

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menukita.adapter.ImagePagerAdapter
import com.example.menukita.adapter.ThumbnailAdapter
import com.example.menukita.databinding.ActivityMenuDetailBinding
import com.example.menukita.model.Menu
import com.example.menukita.util.CartManager
import com.example.menukita.util.FavoriteManager
import com.example.menukita.util.TextStyler
import com.example.menukita.util.UiFeedback
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class MenuDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuDetailBinding
    private var qty = 1
    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("MENU_ID")
        val name = intent.getStringExtra("MENU_NAMA") ?: "Menu"
        val price = intent.getIntExtra("MENU_HARGA", 0)
        val desc = intent.getStringExtra("MENU_DESKRIPSI") ?: ""
        val kategori = intent.getStringExtra("MENU_KATEGORI")
        val imageUrl = intent.getStringExtra("MENU_IMAGE_URL") ?: ""

        menu = Menu(id = id, nama = name, harga = price, deskripsi = desc, kategori = kategori, imageUrl = imageUrl)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.title = name
        setupSwipeBackGesture()

        val overlayColor = when (kategori) {
            "Makanan" -> R.color.overlay_orange
            "Minuman" -> R.color.overlay_green
            "Dessert" -> R.color.overlay_gold
            else -> R.color.overlay_orange
        }
        binding.collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, overlayColor))

        val transitionName = intent.getStringExtra("TRANSITION_NAME") ?: "menu_image"
        ViewCompat.setTransitionName(binding.vpImages, transitionName)

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        binding.tvMenuName.text = name
        binding.tvMenuPrice.text = formatter.format(price)
        TextStyler.applyGradient(
            binding.tvMenuPrice,
            ContextCompat.getColor(this, R.color.primary_orange),
            ContextCompat.getColor(this, R.color.gold_accent)
        )
        binding.tvMenuDesc.text = desc

        val imageUrls = listOf(imageUrl, imageUrl, imageUrl).filter { it.isNotBlank() }
        val images = if (imageUrls.isEmpty()) listOf("") else imageUrls
        binding.vpImages.adapter = ImagePagerAdapter(images)
        binding.rvThumbnails.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvThumbnails.adapter = ThumbnailAdapter(images) { index ->
            binding.vpImages.currentItem = index
        }

        binding.btnQtyMinus.setOnClickListener {
            binding.btnQtyMinus.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            if (qty > 1) qty -= 1
            binding.tvQty.text = qty.toString()
            binding.tvQty.animate().scaleX(1.1f).scaleY(1.1f).setDuration(80)
                .withEndAction { binding.tvQty.animate().scaleX(1f).scaleY(1f).setDuration(80).start() }
                .start()
        }
        binding.btnQtyPlus.setOnClickListener {
            binding.btnQtyPlus.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            qty += 1
            binding.tvQty.text = qty.toString()
            binding.tvQty.animate().scaleX(1.1f).scaleY(1.1f).setDuration(80)
                .withEndAction { binding.tvQty.animate().scaleX(1f).scaleY(1f).setDuration(80).start() }
                .start()
        }

        updateFavoriteState()
        binding.btnFavorite.setOnClickListener {
            FavoriteManager.toggle(menu)
            updateFavoriteState()
            binding.btnFavorite.animate().scaleX(1.05f).scaleY(1.05f).setDuration(120)
                .withEndAction { binding.btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start() }
                .start()
            UiFeedback.showToast(this, "Favorit diperbarui", UiFeedback.Type.INFO)
        }

        binding.btnShare.setOnClickListener {
            val shareText = "Coba menu $name di MenuKita! Harga: ${formatter.format(price)}"
            val intentShare = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intentShare, "Bagikan Menu"))
        }

        binding.btnAddToCart.setOnClickListener {
            binding.btnAddToCart.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            CartManager.add(menu, qty)
            binding.btnAddToCart.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.success_pop))
            UiFeedback.showToast(this, "Ditambahkan ke keranjang", UiFeedback.Type.SUCCESS)
        }
    }

    private fun updateFavoriteState() {
        val isFav = FavoriteManager.isFavorite(menu.id)
        binding.btnFavorite.text = if (isFav) "Favorit (aktif)" else "Favorit"
    }

    private fun setupSwipeBackGesture() {
        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val edge = resources.displayMetrics.density * 24f
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (e1.x <= edge && diffX > 120 && abs(diffX) > abs(diffY) && velocityX > 700f) {
                    onBackPressedDispatcher.onBackPressed()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    return true
                }
                return false
            }
        })
        binding.detailRoot.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            false
        }
    }
}
