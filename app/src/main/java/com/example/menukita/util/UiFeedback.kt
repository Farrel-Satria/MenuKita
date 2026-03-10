package com.example.menukita.util

import android.content.Context
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.menukita.R

object UiFeedback {
    enum class Type { SUCCESS, ERROR, INFO }

    fun showToast(context: Context, message: String, type: Type) {
        val view = LayoutInflater.from(context).inflate(R.layout.toast_custom, null)
        val tvText = view.findViewById<TextView>(R.id.tvToastText)
        val ivIcon = view.findViewById<ImageView>(R.id.ivToastIcon)
        tvText.text = message

        when (type) {
            Type.SUCCESS -> {
                ivIcon.setImageResource(android.R.drawable.btn_star_big_on)
                ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.success_green))
            }
            Type.ERROR -> {
                ivIcon.setImageResource(android.R.drawable.ic_delete)
                ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.error_red))
            }
            Type.INFO -> {
                ivIcon.setImageResource(android.R.drawable.ic_dialog_info)
                ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary_orange))
            }
        }

        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.toast_pop_in))

        Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            this.view = view
            show()
        }
    }
}
