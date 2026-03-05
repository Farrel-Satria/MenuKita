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
import java.text.NumberFormat
import java.util.Locale

class MenuGridAdapter(
    private var menuList: List<Menu>,
    private val onItemClick: (Menu) -> Unit
) : RecyclerView.Adapter<MenuGridAdapter.GridViewHolder>() {

    class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val tvNama: TextView = view.findViewById(R.id.tvNamaMenu)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaMenu)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDeskripsiMenu)
        val tvKategori: TextView = view.findViewById(R.id.tvKategoriBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_grid, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val menu = menuList[position]
        holder.tvNama.text = menu.nama
        holder.tvDeskripsi.text = menu.deskripsi ?: ""
        holder.tvKategori.text = menu.kategori ?: "Lainnya"

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvHarga.text = formatter.format(menu.harga ?: 0)

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
