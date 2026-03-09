package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menukita.adapter.OrderAdapter
import com.example.menukita.databinding.ActivityOrderHistoryBinding
import com.example.menukita.repository.OrderRepository

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var orderAdapter: OrderAdapter
    private val orderRepository = OrderRepository()
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        setupToolbar()
        setupRecycler()
        loadOrders()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecycler() {
        orderAdapter = OrderAdapter(emptyList())
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        if (userEmail.isBlank()) {
            Toast.makeText(this, "Email user tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        orderRepository.getOrdersByUserEmail(
            userEmail = userEmail,
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
