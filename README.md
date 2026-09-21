# 🔥 Preguntas Calientes 2.0 — Party Game

Aplicación Android nativa (Kotlin) que convierte cualquier reunión en una fiesta:
**cartas con efectos, 11 modos de juego, XP, rachas, retos, eventos, logros y
una Dark Party UI con 5 temas, sonidos y vibración.** 100% offline.

![Version](https://img.shields.io/badge/version-2.1.0-FF6B9D)
![Min SDK](https://img.shields.io/badge/minSDK-24-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple)
![Build](https://github.com/junmoxia415-droid/preguntas-calientes/actions/workflows/android-build.yml/badge.svg)

---

## 👨‍💻 Desarrollador

**Airien Yolexis Rojas Roque**
🎨 **Studio Lexair** — 📧 airien.rojas@studiolexair.com

---

## 🚀 Qué hay de nuevo en la 2.0

| Sistema | Detalle |
|---|---|
| 🎮 **11 modos** | Calientes · Verdad o Reto · Pareja · Parejas · Solo Diversión · Atrevido · Confesiones · Cartas Especiales · Duelo ⚔️ · Todos contra todos · Aleatorio |
| 🃏 **Cartas con efectos** | FLIP 3D, Doble turno, Cambiar jugador, ⚡ Todos responden, 🎯 Elige, 🃏 Comodín, 💀 Riesgo, 👑 Rey de la ronda, Beso 💋 |
| ❤️ **Progresión** | XP, puntos, niveles, rachas con multiplicadores x1→x5 |
| 🔥 **Rachas** | 🔥x3 (x2) → 🔥🔥x5 (x3) → 💀 IMPARABLE x10 (x5) |
| 🎯 **Retos** | 45 retos físicos/grupales con recompensa de XP |
| 🎲 **Eventos + rondas especiales** | Cada 10 turnos 🎉 ESPECIAL · 🔥 CALIENTE · 👑 FINAL |
| 🛡️ **Pasar + Comodines** | 3 pasos y 3 comodines por jugador — *nadie está obligado a nada* |
| ⏱️ **Temporizador** | ∞ / 10 / 20 / 30 / 60 seg |
| ✍️ **Preguntas personalizadas** | Créalas y se mezclan al mazo (Room) |
| 📚 **Colección + Packs** | 196 preguntas + progreso de descubrimiento |
| 🏆 **12 Logros + 📊 Estadísticas** | Globales y por jugador |
| 🎡 **Ruleta** | Que el azar elija el modo |
| 🎨 **5 temas Dark Party** | 🌸 Love · 🌙 Midnight · 🔥 Inferno · 🎉 Party · 💎 Premium |
| 🔊 **Sonido + 📳 Haptics** | 10 SFX + música chill/intensa dinámica (sintetizada, offline) |
| 💾 **Continuar partida** | Guardado automático por turno |
| 🏡 **Modo familiar** | Filtro de contenido + confirmación de intensidad extrema |

📜 **Documento de mejoras completo:** [`docs/MEJORAS.md`](docs/MEJORAS.md)

---

## 🎮 Cómo se juega

1. **JUGAR** → agrega jugadores (nombre, avatar, pareja sí/no, pronombre)
2. Elige **modo**, categorías, intensidad y temporizador
3. **3-2-1-🔥GO** → toca la carta para descubrirla (FLIP)
4. Responde honestamente, completa el reto... o **PASAR** 🛡️
5. Gana **XP**, mantén la **racha**, desbloquea **logros**
6. Al terminar: **podio + confeti + resultados**

> 🤝 Regla de oro: nadie está obligado a responder nada que no quiera. PASAR es gratis (3 por partida) y también puedes usar comodines.

---

## 🛠️ Compilar en local

```bash
# Requisitos: JDK 17 y Android SDK (o Android Studio)
chmod +x gradlew
./gradlew assembleDebug     # APK instalable -> app/build/outputs/apk/debug/
./gradlew assembleRelease   # Release      -> app/build/outputs/apk/release/
./gradlew test              # Tests
./gradlew lint              # Análisis lint
```

---

## ⚙️ CI/CD — Compilación automática del APK con GitHub Actions

Cada push compila la app en GitHub mediante [`.github/workflows/android-build.yml`](.github/workflows/android-build.yml):

### 🔄 ¿Cuándo se ejecuta?

| Evento | Resultado |
|---|---|
| Push a `main` o `develop` | Compila + tests + lint + artefactos |
| Pull Request a `main` | Validación (build + tests + lint) |
| **Push a `main`** | Además crea un **Release** `v1.0.N` con el APK |
| Manual | Botón **Run workflow** en la pestaña Actions |

### 🏗️ ¿Qué hace el pipeline?

1. **`build` job** (ubuntu-latest + JDK 17 + Gradle 8.4 + KSP/Room):
   - `./gradlew build` → compila · `./gradlew test` → tests
   - `./gradlew assembleDebug` → **APK instalable** (firmada con keystore debug)
   - `./gradlew assembleRelease` → APK release (sin firmar)
   - Sube ambos APK como *Artifacts* (30 días) y crea **Release** con el APK debug
2. **`lint` job**: análisis estático y reporte HTML en *Artifacts*

### 📥 ¿Dónde descargar el APK?

- **Releases:** pestaña *Releases* del repositorio → descarga **`Preguntas-Calientes-v2.1.0.apk`** (instalable directo ✅)
- **Artifacts:** pestaña *Actions* → elige el *run* → *Artifacts* → `Preguntas-Calientes-APK`

> 📌 Los APK siempre llevan el nombre del juego: `Preguntas-Calientes-v<versión>.apk`
> (nada de `app-debug.apk` — lo que descargan tus amigos se ve profesional 😎)

### 🔐 Nota sobre la firma

El APK **debug** se firma con el keystore de desarrollo: instalable y listo para
jugar. Para **Google Play** genera tu keystore y configura `signingConfigs` en
`app/build.gradle.kts` (+ revisar clasificación de edad del contenido).

---

## 🏗️ Arquitectura (v2.0)

```
com.studiolexair.preguntascalientes
├── PreguntasApp.kt          # Application (DB + sonido)
├── data/
│   ├── db/                  # Room: entidades, DAOs, StatsRepository, SessionRepository
│   └── questions/           # PacksData (contenido) + QuestionRepository (selección inteligente)
├── domain/
│   ├── model/               # Player, Question, Category, GameMode, CardType, GameCard, Challenge, Achievement, GameConfig, LevelSystem
│   └── game/                # GameEngine (máquina de estados de la partida)
├── audio/
│   └── SoundManager.kt      # SFX + música dinámica por intensidad
├── ui/                      # 14 pantallas (BaseActivity con temas + adapters)
└── utils/
    ├── GameSession.kt       # Estado compartido
    ├── PrefsManager.kt      # Ajustes (volúmenes, tema, familiar, timer)
    ├── HapticsHelper.kt
    └── views/               # ParticleView, ConfettiView, RouletteView
```

**Stack:** Kotlin · MVVM · ViewBinding · Room (KSP) · Gson · MediaPlayer · Coroutines

---

## 📄 Licencia

© 2024 Studio Lexair — Todos los derechos reservados.
**Desarrollado por Airien Yolexis Rojas Roque.**

> Hecho con ❤️ para compartir con amigos

### 🌟 ¡Si te gusta el proyecto, dale una estrella! ⭐
