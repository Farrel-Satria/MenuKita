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
import com.example.menukita.repository.UserRepository
import com.example.menukita.model.User
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()
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
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.email != null) {
                // User is already logged in, fetch details to determine role
                userRepository.getUserByEmail(currentUser.email!!) { user, _ ->
                    if (user != null) {
                        val intent = if (user.role == "admin") {
                            Intent(this, MainActivity::class.java).apply {
                                putExtra("USER_ROLE", user.role)
                                putExtra("USER_NAME", user.name)
                                putExtra("USER_EMAIL", user.email)
                            }
                        } else {
                            Intent(this, UserDashboardActivity::class.java).apply {
                                putExtra("USER_NAME", user.name)
                                putExtra("USER_EMAIL", user.email)
                            }
                        }
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    } else {
                        // Profile missing? Go to login
                        goToEntrance()
                    }
                }
            } else {
                goToEntrance()
            }
        }, 1800)
    }

    private fun goToEntrance() {
        val next = if (OnboardingPrefs.isDone(this)) {
            LoginActivity::class.java
        } else {
            OnboardingActivity::class.java
        }
        startActivity(Intent(this, next))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
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
