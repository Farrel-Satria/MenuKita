package com.example.menukita.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.max
import kotlin.random.Random

class AmbientParticlesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class Mode { FLOATING, RAIN, SNOW }

    private data class Dot(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var size: Float,
        var phase: Float
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(com.example.menukita.R.color.particle_soft)
    }
    private val dots = mutableListOf<Dot>()
    private var animator: ValueAnimator? = null
    private var mode: Mode = Mode.FLOATING

    fun setMode(newMode: Mode) {
        if (mode == newMode) return
        mode = newMode
        resetParticles(width, height)
    }

    fun start() {
        if (width <= 0 || height <= 0) return
        if (animator?.isRunning == true) return
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                step()
                invalidate()
            }
            start()
        }
    }

    fun stop() {
        animator?.cancel()
        animator = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetParticles(w, h)
    }

    private fun resetParticles(w: Int, h: Int) {
        dots.clear()
        if (w <= 0 || h <= 0) return
        val count = when (mode) {
            Mode.FLOATING -> max(22, w / 42)
            Mode.RAIN -> max(30, w / 24)
            Mode.SNOW -> max(24, w / 30)
        }
        repeat(count) {
            val x = Random.nextFloat() * w
            val y = Random.nextFloat() * h
            val dot = when (mode) {
                Mode.FLOATING -> Dot(
                    x = x,
                    y = y,
                    vx = Random.nextFloat() * 0.8f - 0.4f,
                    vy = -(Random.nextFloat() * 0.7f + 0.25f),
                    size = Random.nextFloat() * 3f + 1.8f,
                    phase = Random.nextFloat() * 6.28f
                )
                Mode.RAIN -> Dot(
                    x = x,
                    y = y,
                    vx = Random.nextFloat() * 0.9f - 0.45f,
                    vy = Random.nextFloat() * 5.5f + 6f,
                    size = Random.nextFloat() * 10f + 8f,
                    phase = 0f
                )
                Mode.SNOW -> Dot(
                    x = x,
                    y = y,
                    vx = Random.nextFloat() * 0.6f - 0.3f,
                    vy = Random.nextFloat() * 1.2f + 0.7f,
                    size = Random.nextFloat() * 4f + 2.5f,
                    phase = Random.nextFloat() * 6.28f
                )
            }
            dots.add(dot)
        }
        invalidate()
    }

    private fun step() {
        if (width == 0 || height == 0) return
        dots.forEach { d ->
            when (mode) {
                Mode.FLOATING -> {
                    d.phase += 0.03f
                    d.x += d.vx + (cos(d.phase.toDouble()).toFloat() * 0.15f)
                    d.y += d.vy
                    if (d.y < -10f) d.y = height + 10f
                    if (d.x < -10f) d.x = width + 10f
                    if (d.x > width + 10f) d.x = -10f
                }
                Mode.RAIN -> {
                    d.x += d.vx
                    d.y += d.vy
                    if (d.y > height + 12f) {
                        d.y = -12f
                        d.x = Random.nextFloat() * width
                    }
                }
                Mode.SNOW -> {
                    d.phase += 0.035f
                    d.x += d.vx + (cos(d.phase.toDouble()).toFloat() * 0.5f)
                    d.y += d.vy
                    if (d.y > height + 10f) {
                        d.y = -10f
                        d.x = Random.nextFloat() * width
                    }
                    if (d.x < -10f) d.x = width + 10f
                    if (d.x > width + 10f) d.x = -10f
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dots.forEach { d ->
            when (mode) {
                Mode.RAIN -> {
                    paint.alpha = 130
                    paint.strokeWidth = 2f
                    canvas.drawLine(d.x, d.y, d.x - (d.vx * 2), d.y - d.size, paint)
                }
                Mode.FLOATING, Mode.SNOW -> {
                    paint.alpha = if (mode == Mode.FLOATING) 120 else 170
                    canvas.drawCircle(d.x, d.y, d.size, paint)
                }
            }
        }
    }
}
