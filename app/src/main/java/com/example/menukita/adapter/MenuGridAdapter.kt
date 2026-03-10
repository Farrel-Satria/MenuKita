package com.example.menukita.adapter

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.menukita.R
import com.example.menukita.model.Menu
import com.example.menukita.util.FavoriteManager
import com.example.menukita.util.TextEffects
import androidx.core.view.ViewCompat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class MenuGridAdapter(
    private var menuList: List<Menu>,
    private val onItemClick: (Menu, ImageView) -> Unit,
    private val onToggleFavorite: (Menu, View) -> Unit,
    private val onLongPress: (Menu, View) -> Unit,
    private val onFlickAction: (Menu, QuickAction, View) -> Unit
) : RecyclerView.Adapter<MenuGridAdapter.GridViewHolder>() {

    enum class QuickAction {
        ADD_TO_CART,
        TOGGLE_FAVORITE
    }

    inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val ivFavorite: ImageView = view.findViewById(R.id.ivFavorite)
        val viewOverlay: View = view.findViewById(R.id.viewOverlay)
        val viewGlow: View = view.findViewById(R.id.viewGlow)
        val tvNama: TextView = view.findViewById(R.id.tvNamaMenu)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaMenu)
        val tvHargaLama: TextView = view.findViewById(R.id.tvHargaLama)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDeskripsiMenu)
        val tvKategori: TextView = view.findViewById(R.id.tvKategoriBadge)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        var currentMenu: Menu? = null
        private val gestureDetector: GestureDetector
        private val touchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
        private var downX = 0f
        private var downY = 0f
        private var verticalScrollDetected = false

        init {
            gestureDetector = GestureDetector(view.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean = true

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    springBounce(view)
                    currentMenu?.let { menu -> onItemClick(menu, ivMenu) }
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    springBounce(view)
                    currentMenu?.let { menu -> onToggleFavorite(menu, view) }
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    springBounce(view)
                    currentMenu?.let { menu -> onLongPress(menu, view) }
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (e1 == null || verticalScrollDetected) return false
                    val diffX = e2.x - e1.x
                    val diffY = e2.y - e1.y
                    if (abs(diffX) > abs(diffY) && abs(diffX) > 90 && abs(velocityX) > 900f) {
                        springBounce(view)
                        currentMenu?.let { menu ->
                            val action = if (diffX > 0) QuickAction.ADD_TO_CART else QuickAction.TOGGLE_FAVORITE
                            onFlickAction(menu, action, view)
                        }
                        return true
                    }
                    return false
                }
            })

            view.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.x
                        downY = event.y
                        verticalScrollDetected = false
                        view.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.x - downX
                        val dy = event.y - downY
                        if (!verticalScrollDetected && abs(dy) > abs(dx) && abs(dy) > touchSlop) {
                            verticalScrollDetected = true
                            view.parent.requestDisallowInterceptTouchEvent(false)
                            resetTilt(view)
                        } else if (!verticalScrollDetected) {
                            applyTilt(view, event.x, event.y)
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.parent.requestDisallowInterceptTouchEvent(false)
                        resetTilt(view)
                    }
                }
                if (!verticalScrollDetected) {
                    gestureDetector.onTouchEvent(event)
                }
                false
            }
        }

        private fun applyTilt(target: View, touchX: Float, touchY: Float) {
            val centerX = target.width / 2f
            val centerY = target.height / 2f
            if (centerX <= 0f || centerY <= 0f) return
            val rotY = ((touchX - centerX) / centerX) * 3.2f
            val rotX = -((touchY - centerY) / centerY) * 3.2f
            target.rotationX = rotX.coerceIn(-4f, 4f)
            target.rotationY = rotY.coerceIn(-4f, 4f)
            target.scaleX = 1.008f
            target.scaleY = 1.008f
        }

        private fun resetTilt(target: View) {
            target.animate().rotationX(0f).rotationY(0f).scaleX(1f).scaleY(1f)
                .setDuration(180)
                .setInterpolator(OvershootInterpolator(0.8f))
                .start()
        }

        private fun springBounce(target: View) {
            target.animate().cancel()
            target.animate().scaleX(0.93f).scaleY(0.93f).setDuration(80).withEndAction {
                target.animate().scaleX(1f).scaleY(1f).setDuration(220)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_grid, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val menu = menuList[position]
        holder.currentMenu = menu
        ViewCompat.setTransitionName(holder.ivMenu, "menu_image_${menu.id ?: position}")
        holder.tvNama.text = menu.nama
        val desc = menu.deskripsi?.trim().orEmpty()
        holder.tvDeskripsi.text = if (desc.isBlank()) "Menu favorit pelanggan" else desc
        holder.tvKategori.text = menu.kategori ?: "Lainnya"

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val price = menu.harga ?: 0
        holder.tvHarga.text = formatter.format(price)
        val isFeatured = position % 3 == 0
        if (isFeatured) {
            holder.tvHargaLama.visibility = View.VISIBLE
            TextEffects.strikethrough(holder.tvHargaLama, formatter.format((price * 1.16f).toInt()))
        } else {
            holder.tvHargaLama.visibility = View.GONE
            holder.tvHargaLama.paintFlags = holder.tvHargaLama.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        holder.tvRating.text = String.format(Locale("id", "ID"), "%.1f", 4.4 + (position % 5) * 0.1)
        holder.ivFavorite.visibility = if (FavoriteManager.isFavorite(menu.id)) View.VISIBLE else View.GONE
        holder.viewGlow.visibility = if (isFeatured) View.VISIBLE else View.GONE

        val overlayColor = when (menu.kategori) {
            "Makanan" -> R.color.overlay_orange
            "Minuman" -> R.color.overlay_green
            "Dessert" -> R.color.overlay_gold
            else -> R.color.overlay_orange
        }
        holder.viewOverlay.background.mutate().setTint(holder.itemView.context.getColor(overlayColor))

        Glide.with(holder.itemView.context)
            .load(menu.imageUrl)
            .placeholder(R.drawable.placeholder_food)
            .error(R.drawable.placeholder_food)
            .into(holder.ivMenu)

    }

    override fun getItemCount(): Int = menuList.size

    fun updateData(newList: List<Menu>) {
        menuList = newList
        notifyDataSetChanged()
    }
}
