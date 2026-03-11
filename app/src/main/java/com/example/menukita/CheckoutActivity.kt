package com.example.menukita

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.menukita.adapter.CartAdapter
import com.example.menukita.databinding.ActivityCheckoutBinding
import com.example.menukita.model.Order
import com.example.menukita.model.OrderItem
import com.example.menukita.model.Menu
import com.example.menukita.repository.OrderRepository
import com.example.menukita.util.CartItem
import com.example.menukita.util.CartManager
import com.example.menukita.util.UiFeedback
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
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
        setupOrderTypeSelection()
        setupPlaceOrder()
    }

    private fun setupOrderTypeSelection() {
        binding.rgOrderType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbTakeAway) {
                binding.tilAddress.visibility = android.view.View.VISIBLE
            } else {
                binding.tilAddress.visibility = android.view.View.GONE
            }
        }
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
        attachSwipeToDelete()
        updateEmptyState()
    }

    private fun attachSwipeToDelete() {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                CartManager.move(from, to)
                cartAdapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removed = CartManager.getItems().getOrNull(position) ?: return
                CartManager.remove(removed.menuId)
                cartAdapter.updateData(CartManager.getItems())
                bindSummary()

                Snackbar.make(binding.root, "Item dihapus", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        val menu = Menu(
                            id = removed.menuId,
                            nama = removed.name,
                            harga = removed.price,
                            kategori = removed.category,
                            imageUrl = removed.imageUrl
                        )
                        CartManager.add(menu, removed.qty)
                        cartAdapter.updateData(CartManager.getItems())
                        bindSummary()
                    }
                    .show()
            }
        })
        helper.attachToRecyclerView(binding.rvCart)
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
        binding.lottieEmptyCart.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun setupPlaceOrder() {
        binding.btnPlaceOrder.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                UiFeedback.showToast(this, "Keranjang masih kosong", UiFeedback.Type.INFO)
                return@setOnClickListener
            }

            if (userEmail.isBlank()) {
                UiFeedback.showToast(this, "Email user tidak ditemukan", UiFeedback.Type.ERROR)
                return@setOnClickListener
            }

            val paymentMethod = if (binding.rbTransfer.isChecked) "Transfer Bank" else "Tunai"
            val orderType = if (binding.rbTakeAway.isChecked) "Bungkus" else "Makan Sini"
            val notes = binding.etNotes.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            if (orderType == "Bungkus" && address.isEmpty()) {
                UiFeedback.showToast(this, "Harap isi alamat pengiriman untuk pesanan bungkus", UiFeedback.Type.ERROR)
                return@setOnClickListener
            }

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
                orderType = orderType,
                notes = notes,
                address = address,
                createdAt = System.currentTimeMillis()
            )

            binding.btnPlaceOrder.isEnabled = false
            orderRepository.addOrder(order) { success ->
                binding.btnPlaceOrder.isEnabled = true
                if (success) {
                    CartManager.clear()
                    binding.btnPlaceOrder.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.success_pop))
                    UiFeedback.showToast(this, "Pesanan berhasil dibuat", UiFeedback.Type.SUCCESS)
                    val intent = android.content.Intent(this, OrderConfirmationActivity::class.java).apply {
                        putExtra("ORDER_TOTAL", total)
                        putExtra("ORDER_PAYMENT", paymentMethod)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    UiFeedback.showToast(this, "Gagal membuat pesanan", UiFeedback.Type.ERROR)
                }
            }
        }
    }
}
