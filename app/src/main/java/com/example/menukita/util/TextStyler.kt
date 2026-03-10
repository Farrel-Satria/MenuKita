package com.example.menukita.util

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

object TextStyler {
    fun applyGradient(textView: TextView, startColor: Int, endColor: Int) {
        textView.post {
            val width = textView.paint.measureText(textView.text.toString())
            val shader = LinearGradient(
                0f, 0f, width, textView.textSize,
                startColor, endColor, Shader.TileMode.CLAMP
            )
            textView.paint.shader = shader
            textView.invalidate()
        }
    }
}
