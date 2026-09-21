package com.studiolexair.preguntascalientes.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextThemeWrapper
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import com.studiolexair.preguntascalientes.R

/**
 * PartyDialog — Ventanas modales propias del juego (estilo Dark Party).
 * Reemplaza los diálogos grises del sistema en TODA la app.
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
object PartyDialog {

    /** Builder ya temático (surface oscura, textos claros, acento del tema). */
    fun builder(activity: Activity): AlertDialog.Builder =
        AlertDialog.Builder(ContextThemeWrapper(activity, R.style.PartyAlertDialog))

    /** Muestra el diálogo aplicando el fondo redondeado Dark Party. */
    fun AlertDialog.Builder.showParty(): AlertDialog {
        val dialog = show()
        decorate(dialog)
        return dialog
    }

    fun decorate(dialog: AlertDialog) {
        dialog.window?.let { w ->
            w.setBackgroundDrawable(
                AppCompatResources.getDrawable(dialog.context, R.drawable.bg_dialog_party)
                    ?: ColorDrawable(Color.TRANSPARENT)
            )
        }
        // Título con color del tema si está disponible (appcompat o material)
        try {
            val title = dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)
                ?: dialog.findViewById<TextView>(android.R.id.title)
            title?.apply {
                setTextColor(resolveAccent(dialog))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
        } catch (_: Exception) {}
    }

    private fun resolveAccent(dialog: AlertDialog): Int {
        val ta = dialog.context.theme.obtainStyledAttributes(intArrayOf(androidx.appcompat.R.attr.colorPrimary))
        val c = ta.getColor(0, 0xFFFF6B9D.toInt())
        ta.recycle()
        return c
    }
}
