package com.example.menukita.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ParticleBurstView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Piece(
        var x: Float,
        var y: Float,
        val vx: Float,
        val vy: Float,
        var life: Float,
        val color: Int,
        val radius: Float
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pieces = mutableListOf<Piece>()
    private var animator: ValueAnimator? = null

    fun emitAt(x: Float, y: Float, color: Int? = null) {
        val colors = listOf(
            context.getColor(com.example.menukita.R.color.primary_orange),
            context.getColor(com.example.menukita.R.color.gold_accent),
            context.getColor(com.example.menukita.R.color.accent_green)
        )
        val chosen = color ?: colors.random()
        repeat(18) { idx ->
            val angle = (idx / 18f) * (Math.PI * 2f)
            val speed = Random.nextFloat() * 4f + 1.8f
            pieces += Piece(
                x = x,
                y = y,
                vx = cos(angle).toFloat() * speed,
                vy = sin(angle).toFloat() * speed,
                life = 1f,
                color = if (idx % 3 == 0) chosen else colors.random(),
                radius = Random.nextFloat() * 3f + 1.5f
            )
        }
        startIfNeeded()
    }

    private fun startIfNeeded() {
        if (animator?.isRunning == true) return
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 560
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                val dt = 0.07f
                pieces.forEach { p ->
                    p.x += p.vx
                    p.y += p.vy
                    p.life -= dt
                }
                pieces.removeAll { it.life <= 0f }
                if (pieces.isEmpty()) {
                    cancel()
                }
                invalidate()
            }
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
        pieces.forEach { p ->
            paint.color = p.color
            paint.alpha = (255 * p.life).toInt().coerceIn(0, 255)
            canvas.drawCircle(p.x, p.y, p.radius, paint)
        }
    }
}
