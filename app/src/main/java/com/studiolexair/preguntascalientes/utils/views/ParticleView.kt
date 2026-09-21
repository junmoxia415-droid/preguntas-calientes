package com.studiolexair.preguntascalientes.utils.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.studiolexair.preguntascalientes.R
import kotlin.random.Random

/**
 * Fondo animado Dark Party (catálogo §10): degradado + partículas
 * flotantes (estrellas, corazones, destellos) tintadas con el tema.
 * Ligero: máx 26 partículas, sin canvas allocations por frame.
 */
class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private data class Particle(
        var x: Float, var y: Float, var r: Float, var speed: Float,
        var drift: Float, var alpha: Int, val char: String?
    )

    private val particles = mutableListOf<Particle>()
    private val bgPaint = Paint()
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var primaryColor = 0xFFFF6B9D.toInt()
    private var secondaryColor = 0xFFC73866.toInt()
    private var bgColor = 0xFF1A0F16.toInt()
    private var w = 0; private var h = 0

    private var emojis = arrayOf("✨", "❤️", "💫", "🔥")

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 28000L
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener { step(); invalidate() }
    }

    init {
        // Leer colores del tema activo
        val ta = context.theme
        fun attrColor(attr: Int, fallback: Int): Int {
            val a = intArrayOf(attr)
            val t = ta.obtainStyledAttributes(a)
            val c = t.getColor(0, fallback)
            t.recycle()
            return c
        }
        primaryColor = attrColor(androidx.appcompat.R.attr.colorPrimary, primaryColor)
        secondaryColor = attrColor(com.google.android.material.R.attr.colorSecondary, secondaryColor)
        bgColor = attrColor(android.R.attr.colorBackground, bgColor)
        emojiPaint.textSize = 38f
    }

    /** Emojis de partícula según modo (§10: fondos distintos por modo). */
    fun setModeEmoji(vararg e: String) { emojis = e.arrayOf(); buildParticles() }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.w = w; this.h = h
        // Degradado: fondo → mezcla con primario muy tenue
        val end = blend(bgColor, primaryColor, 0.16f)
        bgPaint.shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), bgColor, end, Shader.TileMode.CLAMP)
        buildParticles()
    }

    private fun buildParticles() {
        particles.clear()
        if (w == 0 || h == 0) return
        repeat(22) {
            particles.add(makeParticle(Random.nextFloat() * h))
        }
        repeat(6) {
            particles.add(makeParticle(Random.nextFloat() * h).copy(
                char = emojis.random(), r = 22f + Random.nextFloat() * 14f, speed = 0.6f
            ))
        }
    }

    private fun makeParticle(y: Float): Particle = Particle(
        x = Random.nextFloat() * w, y = y,
        r = 2f + Random.nextFloat() * 6f,
        speed = 0.5f + Random.nextFloat() * 2.2f,
        drift = -0.5f + Random.nextFloat(),
        alpha = 40 + Random.nextInt(150),
        char = null
    )

    private fun step() {
        particles.forEach { p ->
            p.y -= p.speed
            p.x += p.drift + (if (Math.random() < 0.02) (Math.random() - 0.5).toFloat() * 2 else 0f)
            if (p.y < -40) { p.y = h + 20f; p.x = Random.nextFloat() * w }
            if (p.x < -40) p.x = w + 20f
            if (p.x > w + 40) p.x = -20f
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)
        particles.forEach { p ->
            val tint = if (Random.nextInt(2) == 0) primaryColor else secondaryColor
            if (p.char != null) {
                emojiPaint.alpha = (p.alpha * 0.55).toInt()
                canvas.drawText(p.char, p.x, p.y, emojiPaint)
            } else {
                dotPaint.color = tint
                dotPaint.alpha = p.alpha
                canvas.drawCircle(p.x, p.y, p.r, dotPaint)
            }
        }
    }

    override fun onAttachedToWindow() { super.onAttachedToWindow(); animator.start() }
    override fun onDetachedFromWindow() { animator.cancel(); super.onDetachedFromWindow() }

    private fun blend(c1: Int, c2: Int, ratio: Float): Int {
        val a = (android.graphics.Color.alpha(c1) + ratio * (android.graphics.Color.alpha(c2) - android.graphics.Color.alpha(c1))).toInt()
        val r = (android.graphics.Color.red(c1) + ratio * (android.graphics.Color.red(c2) - android.graphics.Color.red(c1))).toInt()
        val g = (android.graphics.Color.green(c1) + ratio * (android.graphics.Color.green(c2) - android.graphics.Color.green(c1))).toInt()
        val b = (android.graphics.Color.blue(c1) + ratio * (android.graphics.Color.blue(c2) - android.graphics.Color.blue(c1))).toInt()
        return android.graphics.Color.argb(a, r, g, b)
    }

    companion object {
        /** Atajo para bindear un ParticleView por id desde una Activity. */
        fun bind(view: View?, modeEmoji: Array<String>? = null) {
            (view as? ParticleView)?.let { pv ->
                modeEmoji?.let { pv.setModeEmoji(*it) }
            }
        }
    }
}
