package com.example.menukita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivityOrderConfirmationBinding
import com.example.menukita.util.TextEffects
import com.example.menukita.util.UiFeedback
import java.text.NumberFormat
import java.util.Locale

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confettiView.start()
        binding.fireworksView.launchBursts(3)
        val total = intent.getIntExtra("ORDER_TOTAL", 0)
        val payment = intent.getStringExtra("ORDER_PAYMENT") ?: "Cash"
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        binding.tvOrderTotal.text = TextEffects.richLabelValue(
            label = "Total:",
            value = formatter.format(total),
            labelColor = getColor(R.color.gray_text),
            valueColor = getColor(R.color.primary_orange)
        )
        binding.tvPaymentMethod.text = TextEffects.richLabelValue(
            label = "Metode:",
            value = payment,
            labelColor = getColor(R.color.gray_text),
            valueColor = getColor(R.color.neutral_dark)
        )
        binding.tvOrderQuote.text = TextEffects.quoteText("Pesananmu sedang kami siapkan dengan sepenuh hati.")
        TextEffects.reveal(binding.tvOrderQuote, 180)
        UiFeedback.showToast(this, "Pesanan berhasil diproses", UiFeedback.Type.SUCCESS)

        binding.btnTrackOrder.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        binding.btnShareOrder.setOnClickListener {
            val shareText = "Pesanan saya di MenuKita sedang diproses."
            val intentShare = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intentShare, "Bagikan Pesanan"))
        }
    }
}
