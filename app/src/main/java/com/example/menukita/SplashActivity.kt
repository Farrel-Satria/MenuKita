package com.example.menukita

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import com.example.menukita.databinding.ActivitySplashBinding
import com.example.menukita.util.OnboardingPrefs

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private val taglines = listOf(
        "Hangatkan harimu dengan menu terbaik.",
        "Dari dapur kami ke mejamu."
    )
    private var taglineIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (binding.root.background as? android.graphics.drawable.AnimationDrawable)?.start()
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.success_pop)
        binding.ivSplashLogo.startAnimation(logoAnim)
        val textAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.tvSplashTitle.startAnimation(textAnim)
        binding.tvSplashTagline.startAnimation(textAnim)
        startLoaderAnimation()
        startProgressAnimation()
        startTaglineCycle()

        handler.postDelayed({
            val next = if (OnboardingPrefs.isDone(this)) {
                LoginActivity::class.java
            } else {
                OnboardingActivity::class.java
            }
            startActivity(Intent(this, next))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, 1800)
    }

    private fun startLoaderAnimation() {
        // Animation will be handled by layout drawable
        try {
            val view = binding.waveLoading as View
            val bg = view.background
            if (bg is AnimationDrawable) {
                bg.start()
            }
        } catch (e: Exception) {
            // Handle animation error gracefully
        }
    }

    private fun startProgressAnimation() {
        binding.splashProgress.isIndeterminate = false
        binding.splashProgress.progress = 0
        ObjectAnimator.ofInt(binding.splashProgress, "progress", 0, 100).apply {
            duration = 1400
            start()
        }
    }

    private fun startTaglineCycle() {
        if (taglines.size < 2) return
        handler.postDelayed({
            val nextIndex = (taglineIndex + 1) % taglines.size
            val fadeOut = ObjectAnimator.ofFloat(binding.tvSplashTagline, "alpha", 0.85f, 0f).apply {
                duration = 250
            }
            val fadeIn = ObjectAnimator.ofFloat(binding.tvSplashTagline, "alpha", 0f, 0.85f).apply {
                duration = 300
            }
            fadeOut.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    taglineIndex = nextIndex
                    binding.tvSplashTagline.text = taglines[taglineIndex]
                }
            })
            AnimatorSet().apply {
                playSequentially(fadeOut, fadeIn)
                start()
            }
        }, 500)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
