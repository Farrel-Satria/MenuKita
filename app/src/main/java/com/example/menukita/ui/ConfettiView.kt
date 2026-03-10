package com.example.menukita.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.random.Random

class ConfettiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Particle(var x: Float, var y: Float, val radius: Float, val speed: Float, val color: Int)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particles = mutableListOf<Particle>()
    private var animator: ValueAnimator? = null

    fun start() {
        if (particles.isEmpty()) return
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2500
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                particles.forEach { p ->
                    p.y += p.speed
                    if (p.y > height + 20) p.y = -20f
                }
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles.clear()
        val count = max(20, w / 20)
        repeat(count) {
            val x = Random.nextFloat() * w
            val y = Random.nextFloat() * h
            val radius = Random.nextInt(3, 7).toFloat()
            val speed = Random.nextInt(4, 10).toFloat()
            val color = Random.nextInt()
            particles.add(Particle(x, y, radius, speed, color))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (p in particles) {
            paint.color = p.color or 0xFF000000.toInt()
            canvas.drawCircle(p.x, p.y, p.radius, paint)
        }
    }
}
