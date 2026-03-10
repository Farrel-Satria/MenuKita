package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menukita.adapter.AdminOrderAdapter
import com.example.menukita.databinding.ActivityAdminOrdersBinding
import com.example.menukita.repository.OrderRepository

class AdminOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrdersBinding
    private lateinit var orderAdapter: AdminOrderAdapter
    private val orderRepository = OrderRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        setupRecycler()
        loadOrders()
    }

    private fun setupRecycler() {
        orderAdapter = AdminOrderAdapter(emptyList()) { order, status ->
            val id = order.id ?: return@AdminOrderAdapter
            orderRepository.updateStatus(id, status) { success ->
                if (!success) {
                    Toast.makeText(this, "Gagal update status", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@AdminOrdersActivity)
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        orderRepository.getAllOrders(
            onDataChange = { orders ->
                val sorted = orders.sortedByDescending { it.createdAt ?: 0L }
                orderAdapter.updateData(sorted)
                binding.tvEmptyOrders.visibility = if (sorted.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
}
