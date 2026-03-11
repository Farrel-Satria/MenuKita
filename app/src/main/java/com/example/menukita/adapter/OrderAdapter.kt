package com.example.menukita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.menukita.R
import com.example.menukita.model.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val context = holder.itemView.context
        
        holder.tvOrderId.text = "Order #${order.id?.take(8)?.uppercase() ?: "-"}"

        val dateText = order.createdAt?.let {
            val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id", "ID"))
            sdf.format(Date(it))
        } ?: "-"
        holder.tvOrderDate.text = dateText

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvOrderTotal.text = formatter.format(order.total ?: 0)
        
        val status = order.status ?: "Menunggu"
        holder.tvOrderStatus.text = status
        
        // Dynamic status coloring
        val statusColor = when (status.lowercase()) {
            "selesai", "dikirim" -> R.color.accent_green
            "batal", "ditolak" -> android.R.color.holo_red_dark
            "diproses" -> R.color.primary_orange
            else -> R.color.gray_text
        }
        holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, statusColor))
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
