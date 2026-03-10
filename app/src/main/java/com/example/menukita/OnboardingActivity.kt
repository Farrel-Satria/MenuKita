package com.example.menukita

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.adapter.OnboardingAdapter
import com.example.menukita.databinding.ActivityOnboardingBinding
import com.example.menukita.model.OnboardingItem
import com.example.menukita.util.OnboardingPrefs
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingItem(
                "Eksplor Menu Favorit",
                "Cari menu terbaik dengan kategori dan rekomendasi.",
                R.drawable.placeholder_food
            ),
            OnboardingItem(
                "Checkout Cepat",
                "Atur pesananmu dan bayar dengan langkah sederhana.",
                R.drawable.placeholder_food
            ),
            OnboardingItem(
                "Lacak Pesanan",
                "Pantau status pesanan dan riwayat order kamu.",
                R.drawable.placeholder_food
            )
        )
        adapter = OnboardingAdapter(items)
        binding.vpOnboarding.adapter = adapter
        TabLayoutMediator(binding.tabOnboarding, binding.vpOnboarding) { _, _ -> }.attach()

        binding.vpOnboarding.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.btnNext.text = if (position == adapter.itemCount - 1) "Mulai" else "Berikutnya"
            }
        })

        binding.btnSkip.setOnClickListener { finishOnboarding() }
        binding.btnNext.setOnClickListener {
            val next = binding.vpOnboarding.currentItem + 1
            if (next < adapter.itemCount) {
                binding.vpOnboarding.currentItem = next
            } else {
                finishOnboarding()
            }
        }
    }

    private fun finishOnboarding() {
        OnboardingPrefs.setDone(this, true)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
