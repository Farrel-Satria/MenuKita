package com.example.menukita

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.menukita.adapter.CartAdapter
import com.example.menukita.databinding.ActivityCheckoutBinding
import com.example.menukita.model.Order
import com.example.menukita.model.OrderItem
import com.example.menukita.repository.OrderRepository
import com.example.menukita.util.CartItem
import com.example.menukita.util.CartManager
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var cartAdapter: CartAdapter
    private val orderRepository = OrderRepository()
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        setupToolbar()
        setupRecycler()
        bindSummary()
        setupPlaceOrder()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecycler() {
        cartAdapter = CartAdapter(CartManager.getItems()) { item: CartItem, newQty: Int ->
            CartManager.updateQty(item.menuId, newQty)
            cartAdapter.updateData(CartManager.getItems())
            bindSummary()
        }
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = cartAdapter
        }
        updateEmptyState()
    }

    private fun bindSummary() {
        val subtotal = CartManager.getTotalPrice()
        val tax = (subtotal * 10) / 100
        val total = subtotal + tax

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        binding.tvSubtotal.text = formatter.format(subtotal)
        binding.tvTax.text = formatter.format(tax)
        binding.tvTotal.text = formatter.format(total)

        updateEmptyState()
    }

    private fun updateEmptyState() {
        val isEmpty = CartManager.getItems().isEmpty()
        binding.tvEmptyCart.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun setupPlaceOrder() {
        binding.btnPlaceOrder.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userEmail.isBlank()) {
                Toast.makeText(this, "Email user tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod = if (binding.rbTransfer.isChecked) "Transfer" else "Cash"
            val items = CartManager.getItems().map {
                OrderItem(
                    menuId = it.menuId,
                    name = it.name,
                    price = it.price,
                    qty = it.qty
                )
            }

            val subtotal = CartManager.getTotalPrice()
            val tax = (subtotal * 10) / 100
            val total = subtotal + tax

            val order = Order(
                id = null,
                userEmail = userEmail,
                items = items,
                total = total,
                status = "Menunggu",
                paymentMethod = paymentMethod,
                createdAt = System.currentTimeMillis()
            )

            binding.btnPlaceOrder.isEnabled = false
            orderRepository.addOrder(order) { success ->
                binding.btnPlaceOrder.isEnabled = true
                if (success) {
                    CartManager.clear()
                    Toast.makeText(this, "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal membuat pesanan", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
