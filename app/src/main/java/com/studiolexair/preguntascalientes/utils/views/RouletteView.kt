package com.studiolexair.preguntascalientes.utils.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.random.Random

/**
 * Ruleta giratoria 🎡 (catálogo §24): decide el modo/categoría.
 */
class RouletteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var labels: List<Pair<String, String>> = emptyList() // emoji to nombre
        set(value) { field = value; invalidate() }

    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f
        textAlign = Paint.Align.CENTER
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF241520.toInt() }
    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFD93D.toInt() }

    private val sliceColors = intArrayOf(
        0xFFFF6B9D.toInt(), 0xFF9D7BFF.toInt(), 0xFF4FC3F7.toInt(),
        0xFF4ADE80.toInt(), 0xFFFFD93D.toInt(), 0xFFFF7A3D.toInt(),
        0xFFF06292.toInt(), 0xFF4DB6AC.toInt(), 0xFFAB47BC.toInt(),
        0xFF78909C.toInt(), 0xFFD4AF37.toInt()
    )

    var currentAngle = 0f
        private set
    private var spinning = false
    private val rect = RectF()

    val isSpinning: Boolean get() = spinning

    /** Gira y notifica el índice ganador (el que queda arriba ↓). */
    fun spin(onDone: (Int) -> Unit) {
        if (spinning || labels.isEmpty()) return
        spinning = true
        val n = labels.size
        val targetIndex = Random.nextInt(n)
        // Queremos que el centro de targetIndex quede apuntando arriba (-90°)
        val sliceDeg = 360f / n
        val finalAngle = (360 * 5) + (270 - (targetIndex * sliceDeg + sliceDeg / 2) - currentAngle % 360).let {
            var a = it % 360; if (a < 0) a += 360; a
        } + currentAngle % 360

        val start = 0f
        ValueAnimator.ofFloat(0f, finalAngle).apply {
            duration = 3400 + Random.nextInt(700)
            interpolator = DecelerateInterpolator(1.6f)
            addUpdateListener {
                currentAngle = it.animatedValue as Float
                invalidate()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    spinning = false
                    onDone(targetIndex)
                }
            })
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val n = labels.size
        if (n == 0) return
        val cx = width / 2f
        val cy = height / 2f
        val radius = (minOf(width, height) / 2f) * 0.9f
        rect.set(cx - radius, cy - radius, cx + radius, cy + radius)

        val sliceDeg = 360f / n
        for (i in 0 until n) {
            slicePaint.color = sliceColors[i % sliceColors.size]
            canvas.drawArc(rect, currentAngle + i * sliceDeg, sliceDeg, true, slicePaint)
            // Emoji en el centro del arco
            val mid = Math.toRadians((currentAngle + i * sliceDeg + sliceDeg / 2).toDouble())
            val ex = cx + (radius * 0.68f) * Math.cos(mid).toFloat()
            val ey = cy + (radius * 0.68f) * Math.sin(mid).toFloat()
            canvas.drawText(labels[i].first, ex, ey + 14f, textPaint)
        }
        // Centro
        canvas.drawCircle(cx, cy, radius * 0.18f, centerPaint)
        // Indicador arriba
        val pointer = Path()
        pointer.moveTo(cx - 22f, cy - radius - 6f)
        pointer.lineTo(cx + 22f, cy - radius - 6f)
        pointer.lineTo(cx, cy - radius + 34f)
        pointer.close()
        canvas.drawPath(pointer, pointerPaint)
    }
}
