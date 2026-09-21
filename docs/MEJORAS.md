# 🔥 Catálogo de Mejoras — Preguntas Calientes 2.0

Documento de seguimiento del **catálogo completo de mejoras**: qué está
implementado en la v2.0 y qué queda en roadmap.

**Desarrollado por Airien Yolexis Rojas Roque — Studio Lexair**

---

## ✅ IMPLEMENTADO en v2.0

### 🎮 Mecánicas de juego
| # | Mejora | Estado | Dónde |
|---|--------|--------|-------|
| 1 | **Sistema de 11 modos** (Calientes, Verdad o Reto, Parejas, Pareja, Diversión, Atrevido, Confesiones, Cartas Especiales, Duelo, Todos, Aleatorio) | ✅ | `GameMode.kt` + `ModesActivity` |
| 2 | **Sistema de cartas** con frente oculto "TOCAR PARA DESCUBRIR" + efectos (Caliente, Extrema, Beso, Confesión, Doble turno, Cambiar jugador, Todos responden, Elige, Comodín, Riesgo, Rey/Reina) | ✅ | `CardType.kt` + `GameEngine.deal()` |
| 3 | **Puntuación**: XP, puntos, racha y nivel por jugador (nivel n = n²·40 XP) | ✅ | `Player.kt`, `LevelSystem.kt` |
| 4 | **Rachas con multiplicadores** x1→x2 (racha 3) →x3 (racha 5) →x5 (racha 10 💀 IMPARABLE) | ✅ | `Player.streakMultiplier`, `GameEngine.resolveSuccess()` |
| 5 | **Retos físicos** (+XP al completarlos, mazo de 45 retos) | ✅ | `PacksData.challenges`, cartas RETO |
| 6 | **Eventos aleatorios** (cada partida tiene probabilidad de cartas especiales; frecuencia escala con nº de jugadores → fiesta/caos) | ✅ | `GameEngine.randomSpecialCard()` |
| 7 | **Perfil de jugador**: nombre, avatar, color, pronombre opcional, estado de relación + stats (partidas, respondidas, retos, racha máx) | ✅ | `Player.kt`, `PlayersActivity` |
| 8 | **Avatares prediseñados** (16 emoji con color asociado, círculo con giro en cambio de turno) | ✅ | `PlayersActivity.buildAvatarChips()` |
| 11 | **Carta FLIP 3D** (rotationY 0→90→0) | ✅ | `GameActivity.flipCard()` |
| 12 | **Animaciones**: entrada escalonada, botones con pulso, 3-2-1-🔥GO, XP flotante, confeti | ✅ | Activities + `ConfettiView` |
| 13 | **Sistema de sonido**: 10 SFX (click, pop, ding, whoosh, tick, flip, evento, fanfarria, cuenta, go) + música chill/intensa, sintetizados offline | ✅ | `audio/SoundManager.kt` + `res/raw/*.wav` |
| 14 | **Haptic feedback** (tap, carta, evento, tick, celebración, fallo) | ✅ | `utils/HapticsHelper.kt` |
| 15 | **Temporizador** (∞/10/20/30/60s, barra + cuenta regresiva + ticks y auto-paso al agotar) | ✅ | `GameActivity.startTimerIfNeeded()` |
| 16 | **Sistema PASAR** (3 pasos/jugador; nadie obligado a responder nunca) | ✅ | `GameEngine.pass()` |
| 17 | **Comodines** (3/jugador: cambiar carta, pasar gratis, cambiar jugador, duplicar puntos, elegir categoría) | ✅ | `GameEngine.Wildcard` |
| 18 | **Colección** (progreso descubierto/total por pack) | ✅ | `CollectionActivity` |
| 19 | **Preguntas personalizadas** (crear/borrar, Room, se mezclan al mazo) | ✅ | `CustomQuestionsActivity` + `CustomQuestionDao` |
| 20 | **Packs** (Original 120 + Parejas 30 + Fiesta 25 + Expansiones 26) | ✅ | `PacksData.kt` |
| 21 | **Modo fiesta/caos** (los eventos son más frecuentes con 5+ y 9+ jugadores) | ✅ | `GameEngine.playerCountBonus()` |
| 22 | **Modo Pareja** (exactamente 2 jugadores, contenido de parejas, validación en setup) | ✅ | `GameMode.PAREJA` |
| 23 | **Modo Duelo** (cada 3 turnos: DUELO → ambos responden → votación A/B → bonus) | ✅ | `GameEngine.duelCard()` + UI votación |
| 24 | **Ruleta** 🎡 (rueda animada que decide el modo) | ✅ | `RouletteView` + `RouletteActivity` |
| 25 | **Selección inteligente de preguntas** (categoría+intensidad+relación+historial → 0 repeticiones por partida) | ✅ | `QuestionRepository` + pools del engine |
| 26 | **Guardar/continuar partida** (auto-guardado por turno, botón CONTINUAR en el menú) | ✅ | `SessionRepository` (Room + Gson) |
| 27 | **Estadísticas** (globales + históricas por jugador) | ✅ | `StatsRepository` + `StatsActivity` |
| 28 | **12 logros** (Primer fuego, Centenario, Sin miedo, Party Master, Imparable, etc.) | ✅ | `Achievement.kt` + unlock en fin de partida |
| 29 | **5 temas** (🌸 Love, 🌙 Midnight, 🔥 Inferno, 🎉 Party, 💎 Premium) aplicados en caliente | ✅ | `themes.xml` + `PrefsManager.AppTheme` |
| 31 | **Nuevo splash** (logo + zoom + partículas + sonido + transición, < 2.5 s) | ✅ | `SplashActivity` |
| 32 | **Nuevo menú principal** (JUGAR, CONTINUAR, Modos, Colección, Logros, Estadísticas, Mis preguntas, Ajustes, Créditos) | ✅ | `MenuActivity` |
| 33 | **Nueva arquitectura** `data/`, `domain/`, `ui/`, `audio/`, `utils/` + MVVM | ✅ | Estructura de paquetes |
| 34 | **Experiencia multi-tamaño** (layouts basados en ScrollView, orientación libre) | ✅ | Layouts |
| 38 | **Control de contenido**: Modo familiar (oculta 🔥🌶️🎭 y capa intensidad) + confirmación de extremo + regla "nadie obligado" | ✅ | `SettingsActivity` + filtros |
| 39 | **Refactor técnico**: QuestionDatabase monolítica → Repository + packs; Session → Engine; ViewBinding; Room | ✅ | Todo el árbol |
| 40 | **Room DB** (preguntas custom, stats, logros, partidas guardadas) — offline-first | ✅ | `data/db/` |
| 42 | **Playlist dinámica**: música chill (suave/medio) / intensa (extremo) | ✅ | `SoundManager.playMusic(intensity)` |
| 43 | **Momentos especiales**: cada 10 turnos 🎉 RONDA ESPECIAL, cada ~25 🔥 CALIENTE (extremo forzado), cada 50 👑 FINAL (doble puntos) | ✅ | `GameEngine.deal()` |
| 44 | **Fin de partida**: ganador con animación, podio 🥇🥈🥉, stats por jugador, logros nuevos, confeti | ✅ | `ResultsActivity` |
| 45 | **Identidad Studio Lexair** (splash + créditos + footer) | ✅ | Vistas |

## 🚧 ROADMAP (próximas versiones)

| # | Mejora | Estado | Etapa |
|---|--------|--------|-------|
| 9/10 | Partículas reactivas al toque + fondos por modo | Parcial (emojis por intensidad ya) | V2.1 |
| 20 | Llegar a **500+ preguntas** (arquitectura de packs lista) | 🚧 196 + 45 retos hoy | V2.2 |
| 30 | Ilustraciones vectoriales propias para iconos | 🚧 (emoji + drawables) | V2.2 |
| 35 | **Multijugador local** (Wi-Fi/Bluetooth) | ⏳ | V2.4 |
| 36 | **Multijugador online** con salas y códigos | ⏳ | V3.0 |
| 37 | **Modo IA** generador de preguntas (opcional, online) | ⏳ | V3.0 |
| 41 | Sincronización de cuentas (Supabase) — offline-first se mantiene | ⏳ | V3.0 |

### Etapas del plan maestro
- **V2.0 — FUNDACIÓN** (entregada ✅): home, navegación, UI dark, cartas, animaciones, sonido, vibración, timer, pasar, jugadores
- **V2.1 — GAMEPLAY** (entregada ✅ adelantada): puntuación, XP, niveles, rachas, retos, comodines, eventos, logros, estadísticas
- **V2.2 — CONTENIDO** (parcial ✅): packs, custom, colección → falta escalar a 500+ e ilustraciones
- **V2.3 — PARTY** (entregada ✅ adelantada): Verdad o Reto, Parejas, Duelo, Ruleta, fiesta, todos
- **V3.0 — ONLINE**: salas, códigos, multijugador, perfiles, sync
