package com.example.menukita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.menukita.R
import com.example.menukita.model.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminOrderAdapter(
    private var orders: List<Order>,
    private val onStatusChange: (Order, String) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder>() {

    class AdminOrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvOrderTotal)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvOrderType: TextView = view.findViewById(R.id.tvOrderType)
        val tvOrderNotes: TextView = view.findViewById(R.id.tvOrderNotes)
        val tvOrderAddress: TextView = view.findViewById(R.id.tvOrderAddress)
        val btnProcess: Button = view.findViewById(R.id.btnProcess)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_order, parent, false)
        return AdminOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Order #${order.id?.take(6) ?: "-"}"
        val dateText = order.createdAt?.let {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            sdf.format(Date(it))
        } ?: "-"
        holder.tvOrderDate.text = dateText

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvOrderTotal.text = formatter.format(order.total ?: 0)
        holder.tvOrderStatus.text = order.status ?: "Menunggu"
        holder.tvOrderType.text = order.orderType ?: "Makan Sini"

        if (!order.notes.isNullOrBlank()) {
            holder.tvOrderNotes.text = "Catatan: ${order.notes}"
            holder.tvOrderNotes.visibility = View.VISIBLE
        } else {
            holder.tvOrderNotes.visibility = View.GONE
        }

        if (!order.address.isNullOrBlank()) {
            holder.tvOrderAddress.text = "Alamat: ${order.address}"
            holder.tvOrderAddress.visibility = View.VISIBLE
        } else {
            holder.tvOrderAddress.visibility = View.GONE
        }

        holder.btnProcess.setOnClickListener { onStatusChange(order, "Diproses") }
        holder.btnComplete.setOnClickListener { onStatusChange(order, "Selesai") }
        holder.btnCancel.setOnClickListener { onStatusChange(order, "Dibatalkan") }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
