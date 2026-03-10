package com.example.menukita.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import java.util.WeakHashMap

object TextEffects {
    private val typingAnimators = WeakHashMap<TextView, ValueAnimator>()
    private val colorAnimators = WeakHashMap<TextView, ValueAnimator>()

    fun reveal(view: View, delay: Long = 0L) {
        view.animate().cancel()
        view.alpha = 0f
        view.translationY = 12f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(260)
            .setInterpolator(OvershootInterpolator(0.7f))
            .start()
    }

    fun typewriter(textView: TextView, text: String, duration: Long = 680L, onEnd: (() -> Unit)? = null) {
        typingAnimators.remove(textView)?.cancel()
        if (text.isEmpty()) {
            textView.text = ""
            onEnd?.invoke()
            return
        }
        val animator = ValueAnimator.ofInt(0, text.length).apply {
            this.duration = duration
            addUpdateListener {
                val length = it.animatedValue as Int
                textView.text = text.substring(0, length.coerceIn(0, text.length))
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
        typingAnimators[textView] = animator
    }

    fun pulseTextColor(textView: TextView, startColor: Int, endColor: Int, duration: Long = 1400L) {
        colorAnimators.remove(textView)?.cancel()
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { textView.setTextColor(it.animatedValue as Int) }
            start()
        }
        colorAnimators[textView] = animator
    }

    fun crossfadeText(textView: TextView, newText: CharSequence) {
        ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f).apply {
            duration = 120
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    textView.text = newText
                    ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
                        duration = 160
                        start()
                    }
                }
            })
            start()
        }
    }

    fun highlightedText(
        text: String,
        keywords: List<String>,
        bgColor: Int,
        textColor: Int
    ): CharSequence {
        val span = SpannableString(text)
        keywords.forEach { keyword ->
            var startIndex = text.indexOf(keyword, ignoreCase = true)
            while (startIndex >= 0) {
                val endIndex = startIndex + keyword.length
                span.setSpan(BackgroundColorSpan(bgColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                span.setSpan(ForegroundColorSpan(textColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                span.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                startIndex = text.indexOf(keyword, startIndex = endIndex, ignoreCase = true)
            }
        }
        return span
    }

    fun richLabelValue(
        label: String,
        value: String,
        labelColor: Int,
        valueColor: Int
    ): CharSequence {
        val result = SpannableStringBuilder("$label $value")
        val split = label.length
        result.setSpan(ForegroundColorSpan(labelColor), 0, split, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(StyleSpan(Typeface.BOLD), 0, split, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(valueColor), split + 1, result.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(RelativeSizeSpan(1.08f), split + 1, result.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return result
    }

    fun quoteText(message: String): CharSequence {
        val quote = "\"$message\""
        val span = SpannableString(quote)
        span.setSpan(StyleSpan(Typeface.ITALIC), 0, quote.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    fun strikethrough(textView: TextView, text: String) {
        textView.text = text
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun clear(textView: TextView) {
        typingAnimators.remove(textView)?.cancel()
        colorAnimators.remove(textView)?.cancel()
        textView.animate().cancel()
    }
}
