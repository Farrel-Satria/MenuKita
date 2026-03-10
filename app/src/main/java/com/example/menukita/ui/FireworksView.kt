package com.example.menukita.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class FireworksView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Spark(
        var x: Float,
        var y: Float,
        val vx: Float,
        val vy: Float,
        var life: Float,
        val color: Int,
        val size: Float
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sparks = mutableListOf<Spark>()
    private var animator: ValueAnimator? = null

    fun launchBursts(burstCount: Int = 3) {
        if (width == 0 || height == 0) {
            post { launchBursts(burstCount) }
            return
        }
        sparks.clear()
        repeat(burstCount) { burstIndex ->
            val centerX = (width * (0.2f + burstIndex * 0.3f)).coerceAtMost(width - 24f)
            val centerY = Random.nextFloat() * (height * 0.45f) + 30f
            createBurst(centerX, centerY)
        }
        startAnimator()
    }

    private fun createBurst(cx: Float, cy: Float) {
        val colors = listOf(
            context.getColor(com.example.menukita.R.color.primary_orange),
            context.getColor(com.example.menukita.R.color.gold_accent),
            context.getColor(com.example.menukita.R.color.accent_green),
            context.getColor(com.example.menukita.R.color.white)
        )
        val count = 26
        repeat(count) {
            val angle = (it.toFloat() / count) * (Math.PI * 2f)
            val speed = Random.nextFloat() * 5f + 3.5f
            sparks.add(
                Spark(
                    x = cx,
                    y = cy,
                    vx = cos(angle).toFloat() * speed,
                    vy = sin(angle).toFloat() * speed,
                    life = 1f,
                    color = colors.random(),
                    size = Random.nextFloat() * 3f + 2f
                )
            )
        }
    }

    private fun startAnimator() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 900
            repeatCount = 0
            addUpdateListener {
                val dt = 0.045f
                sparks.forEach { s ->
                    s.x += s.vx
                    s.y += s.vy
                    s.life -= dt
                }
                sparks.removeAll { it.life <= 0f }
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    sparks.clear()
                    invalidate()
                }
            })
            start()
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        animator = null
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        sparks.forEach { s ->
            paint.color = s.color
            paint.alpha = (s.life * 255).toInt().coerceIn(0, 255)
            canvas.drawCircle(s.x, s.y, s.size, paint)
        }
    }
}
