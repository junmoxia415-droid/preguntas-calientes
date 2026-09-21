package com.studiolexair.preguntascalientes.utils.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * Confeti 🎉 para victorias, logros y fin de partida (catálogo §12 y §44).
 * Llama a [burst] y se apaga solo.
 */
class ConfettiView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private data class Piece(
        var x: Float, var y: Float, var vx: Float, var vy: Float,
        val size: Float, val color: Int, var rotation: Float, val vr: Float
    )

    private val pieces = mutableListOf<Piece>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var running = false
    private val colors = intArrayOf(
        0xFFFF6B9D.toInt(), 0xFFFFD93D.toInt(), 0xFF9D7BFF.toInt(),
        0xFF4ADE80.toInt(), 0xFF4FC3F7.toInt(), 0xFFFF7A3D.toInt()
    )

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 16
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { step() }
    }

    fun burst(amount: Int = 160) {
        if (width == 0) { post { burst(amount) }; return }
        pieces.clear()
        repeat(amount) {
            pieces.add(
                Piece(
                    x = Random.nextFloat() * width,
                    y = -Random.nextFloat() * height * 0.35f,
                    vx = -2.5f + Random.nextFloat() * 5f,
                    vy = 2f + Random.nextFloat() * 7f,
                    size = 8f + Random.nextFloat() * 16f,
                    color = colors.random(),
                    rotation = Random.nextFloat() * 360f,
                    vr = -8f + Random.nextFloat() * 16f
                )
            )
        }
        if (!running) { running = true; animator.start() }
    }

    private fun step() {
        val it = pieces.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.vy += 0.18f // gravedad
            p.vx *= 0.995f
            p.x += p.vx; p.y += p.vy; p.rotation += p.vr
            if (p.y > height + 60) it.remove()
        }
        invalidate()
        if (pieces.isEmpty() && running) { running = false; animator.cancel() }
    }

    override fun onDraw(canvas: Canvas) {
        pieces.forEach { p ->
            paint.color = p.color
            canvas.save()
            canvas.rotate(p.rotation, p.x, p.y)
            canvas.drawRect(p.x - p.size / 2, p.y - p.size / 4, p.x + p.size / 2, p.y + p.size / 4, paint)
            canvas.restore()
        }
    }

    override fun onDetachedFromWindow() { animator.cancel(); super.onDetachedFromWindow() }
}
