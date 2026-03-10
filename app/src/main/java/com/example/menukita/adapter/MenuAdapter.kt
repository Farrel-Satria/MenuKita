package com.example.menukita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.menukita.R
import com.example.menukita.model.Menu
import com.example.menukita.util.FavoriteManager
import java.text.NumberFormat
import java.util.Locale

class MenuAdapter(
    private var menuList: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val ivFavorite: ImageView = view.findViewById(R.id.ivFavorite)
        val viewOverlay: View = view.findViewById(R.id.viewOverlay)
        val tvNama: TextView = view.findViewById(R.id.tvNamaMenu)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaMenu)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDeskripsiMenu)
        val tvKategori: TextView = view.findViewById(R.id.tvKategoriBadge)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.tvNama.text = menu.nama
        holder.tvDeskripsi.text = menu.deskripsi
        holder.tvKategori.text = menu.kategori ?: "Lainnya"
        holder.tvRating.text = String.format(Locale("id", "ID"), "%.1f", 4.5 + (position % 5) * 0.1)
        holder.ivFavorite.visibility = if (FavoriteManager.isFavorite(menu.id)) View.VISIBLE else View.GONE

        val overlayColor = when (menu.kategori) {
            "Makanan" -> R.color.overlay_orange
            "Minuman" -> R.color.overlay_green
            "Dessert" -> R.color.overlay_gold
            else -> R.color.overlay_orange
        }
        holder.viewOverlay.background.mutate().setTint(holder.itemView.context.getColor(overlayColor))
        
        // Format harga ke Rupiah
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvHarga.text = formatter.format(menu.harga ?: 0)

        // Load Gambar dengan Glide
        Glide.with(holder.itemView.context)
            .load(menu.imageUrl)
            .placeholder(R.drawable.placeholder_food)
            .error(R.drawable.placeholder_food)
            .into(holder.ivMenu)

        holder.itemView.setOnClickListener { onItemClick(menu) }
    }

    override fun getItemCount(): Int = menuList.size

    fun updateData(newList: List<Menu>) {
        menuList = newList
        notifyDataSetChanged()
    }
}
