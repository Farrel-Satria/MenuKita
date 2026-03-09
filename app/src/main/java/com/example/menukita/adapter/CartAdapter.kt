package com.example.menukita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.menukita.R
import com.example.menukita.util.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var items: List<CartItem>,
    private val onQtyChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name ?: "Menu"

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvPrice.text = formatter.format(item.price)
        holder.tvQty.text = item.qty.toString()

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.placeholder_food)
            .error(R.drawable.placeholder_food)
            .into(holder.ivMenu)

        holder.btnMinus.setOnClickListener {
            val newQty = item.qty - 1
            onQtyChanged(item, newQty)
        }

        holder.btnPlus.setOnClickListener {
            val newQty = item.qty + 1
            onQtyChanged(item, newQty)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
