package com.example.menukita.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.menukita.R
import com.google.android.material.button.MaterialButton

class TutorialOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.tutorial_overlay)
    }
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = context.resources.displayMetrics.density * 2f
        color = context.getColor(R.color.primary_orange)
    }
    private val targetRect = RectF()
    private val tempRect = Rect()
    private var hasTarget = false
    private var pulseProgress = 0f

    private val pulseAnimator = ValueAnimator.ofFloat(0.6f, 1f).apply {
        duration = 900
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            pulseProgress = it.animatedValue as Float
            invalidate()
        }
    }

    private val tvStep: TextView
    private val tvTitle: TextView
    private val tvMessage: TextView
    private val btnSkip: MaterialButton
    private val btnNext: MaterialButton
    private val card: View

    var onNext: (() -> Unit)? = null
    var onSkip: (() -> Unit)? = null

    init {
        setWillNotDraw(false)
        isClickable = true
        isFocusable = true
        LayoutInflater.from(context).inflate(R.layout.view_tutorial_overlay, this, true)
        tvStep = findViewById(R.id.tvTutorialStep)
        tvTitle = findViewById(R.id.tvTutorialTitle)
        tvMessage = findViewById(R.id.tvTutorialMessage)
        btnSkip = findViewById(R.id.btnTutorialSkip)
        btnNext = findViewById(R.id.btnTutorialNext)
        card = findViewById(R.id.cardTutorial)

        btnSkip.setOnClickListener { onSkip?.invoke() }
        btnNext.setOnClickListener { onNext?.invoke() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!pulseAnimator.isStarted) {
            pulseAnimator.start()
        }
    }

    override fun onDetachedFromWindow() {
        pulseAnimator.cancel()
        super.onDetachedFromWindow()
    }

    fun bind(title: String, message: String, stepIndex: Int, total: Int) {
        tvStep.text = "$stepIndex/$total"
        tvTitle.text = title
        tvMessage.text = message
        btnNext.text = if (stepIndex == total) "Selesai" else "Lanjut"
    }

    fun highlight(target: View) {
        post {
            val rootLocation = IntArray(2)
            getLocationOnScreen(rootLocation)
            target.getGlobalVisibleRect(tempRect)
            val padding = resources.getDimensionPixelSize(R.dimen.tutorial_highlight_padding)
            targetRect.set(
                (tempRect.left - rootLocation[0] - padding).toFloat(),
                (tempRect.top - rootLocation[1] - padding).toFloat(),
                (tempRect.right - rootLocation[0] + padding).toFloat(),
                (tempRect.bottom - rootLocation[1] + padding).toFloat()
            )
            hasTarget = true
            positionCard()
            invalidate()
        }
    }

    private fun positionCard() {
        val params = card.layoutParams as ConstraintLayout.LayoutParams
        val parentHeight = height.toFloat().coerceAtLeast(1f)
        if (targetRect.centerY() > parentHeight * 0.6f) {
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            params.topMargin = resources.getDimensionPixelSize(R.dimen.space_16)
        } else {
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.UNSET
            params.bottomMargin = resources.getDimensionPixelSize(R.dimen.space_16)
        }
        card.layoutParams = params
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!hasTarget) return
        val path = Path().apply {
            addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
            val radius = targetRect.height().coerceAtMost(targetRect.width()) / 2f
            addRoundRect(targetRect, radius, radius, Path.Direction.CCW)
            fillType = Path.FillType.EVEN_ODD
        }
        canvas.drawPath(path, dimPaint)

        highlightPaint.alpha = (160 + 95 * pulseProgress).toInt().coerceIn(80, 255)
        highlightPaint.strokeWidth = (resources.displayMetrics.density * 2f) + (pulseProgress * 3f)
        val radius = targetRect.height().coerceAtMost(targetRect.width()) / 2f
        canvas.drawRoundRect(targetRect, radius, radius, highlightPaint)
    }
}
