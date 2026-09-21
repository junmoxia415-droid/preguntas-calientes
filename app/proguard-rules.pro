# ProGuard — Preguntas Calientes 2.0
# Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair

# ── Modelos Parcelable (resultados, jugadores) ──
-keep class com.studiolexair.preguntascalientes.domain.model.** { *; }

# ── Room ──
-keep class com.studiolexair.preguntascalientes.data.db.** { *; }
-dontwarn androidx.room.**

# ── Gson ──
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.studiolexair.preguntascalientes.data.db.SessionRepository$Snapshot { *; }

# Vistas personalizadas usadas en XML
-keep public class com.studiolexair.preguntascalientes.utils.views.** { public <init>(...); }
