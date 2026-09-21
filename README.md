# 🔥 Preguntas Calientes - Party Game

Aplicación Android nativa para jugar en grupos de amigos con preguntas personalizadas según el estado de relación de cada jugador.

![Version](https://img.shields.io/badge/version-1.0.0-FF6B9D)
![Min SDK](https://img.shields.io/badge/minSDK-24-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple)
![License](https://img.shields.io/badge/license-Proprietary-blue)

---

## 👨‍💻 Desarrollador

**Airien Yolexis Rojas Roque**  
🎨 **Studio Lexair**

- 📧 Email: airien.rojas@studiolexair.com
- 🌐 Studio: Studio Lexair
- 📅 Año: 2024

---

## 🚀 Características

- ✅ **120+ Preguntas** personalizadas y variadas
- ✅ **6 Categorías**: Calientes, Interesantes, Divertidas, Atrevidas, Románticas, Confesiones
- ✅ **Filtrado Inteligente** según estado de relación (con pareja / soltero)
- ✅ **3 Niveles de Intensidad**: Suave 😊, Medio 😏, Extremo 🔥
- ✅ **Interfaz Moderna** con Material Design 3
- ✅ **100% Offline** - No requiere internet ni permisos
- ✅ **Arquitectura MVVM** limpia y escalable
- ✅ **Animaciones Suaves** y experiencia fluida
- ✅ **Soporte Android 7.0+** (API 24 a 34)

---

## 📱 Capturas de Pantalla

| Splash Screen | Menú Principal | Agregar Jugadores |
|---|---|---|
| Logo + Créditos | 4 opciones | Gestión de jugadores |

| Categorías | Juego | Créditos |
|---|---|---|
| 6 categorías + intensidad | Turnos + preguntas | Info desarrollador |

---

## 🎮 Cómo Jugar

1. **Agrega Jugadores**: Mínimo 2 jugadores, indica si tienen pareja
2. **Elige Categorías**: Selecciona entre 6 tipos de preguntas
3. **Ajusta Intensidad**: Desde suave hasta extremo
4. **¡Juega!**: Turnos aleatorios con preguntas personalizadas
5. **Diviértete**: Sé honesto, respeta y disfruta con amigos

### 📖 Reglas de Oro
- Lo que pasa en el juego, se queda en el grupo 😏
- Respeta si alguien no quiere responder
- ¡Sin juzgar, solo divertirse!

---

## 🛠️ Tecnologías

- **Lenguaje**: Kotlin 1.9.22
- **Arquitectura**: MVVM
- **UI**: Material Design 3, ViewBinding, RecyclerView, CardView
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle con Kotlin DSL
- **IDE**: Android Studio Hedgehog+

### 📦 Dependencias Principales
```kotlin
- androidx.core:core-ktx
- androidx.appcompat:appcompat
- com.google.android.material:material
- androidx.lifecycle:lifecycle-viewmodel-ktx
- androidx.recyclerview:recyclerview
- androidx.constraintlayout:constraintlayout
```

---

## 📥 Instalación

### Opción 1: Descargar APK (Recomendado)
1. Ve a la sección [Releases](../../releases)
2. Descarga el último APK `app-release.apk`
3. Instala en tu dispositivo Android (habilita "Fuentes desconocidas")

### Opción 2: Compilar desde código

```bash
# Clonar repositorio
git clone https://github.com/[TU-USUARIO]/preguntas-calientes.git
cd preguntas-calientes

# Compilar APK debug
./gradlew assembleDebug

# Compilar APK release
./gradlew assembleRelease

# El APK estará en:
# app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 📂 Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/studiolexair/preguntascalientes/
│   │   ├── MainActivity.kt (legacy)
│   │   ├── SplashActivity.kt       # Pantalla de bienvenida 2s
│   │   ├── MenuActivity.kt         # Menú principal
│   │   ├── PlayersActivity.kt      # Gestión jugadores
│   │   ├── CategoriesActivity.kt   # Selección categorías
│   │   ├── GameActivity.kt         # Juego principal
│   │   ├── CreditsActivity.kt      # Créditos
│   │   ├── models/
│   │   │   ├── Player.kt           # Modelo jugador
│   │   │   ├── Question.kt         # Modelo pregunta
│   │   │   └── Category.kt         # Enum categorías
│   │   ├── viewmodels/
│   │   │   └── GameViewModel.kt    # MVVM ViewModel
│   │   ├── adapters/
│   │   │   └── PlayersAdapter.kt   # RecyclerView adapter
│   │   └── utils/
│   │       ├── QuestionDatabase.kt # 120 preguntas
│   │       └── GameSession.kt      # Singleton sesión
│   ├── res/
│   │   ├── layout/                 # 6 layouts + item
│   │   ├── drawable/               # Fondos y shapes
│   │   ├── values/                 # colors, strings, themes
│   │   └── mipmap/                 # Iconos launcher
│   └── AndroidManifest.xml
├── build.gradle.kts                # Config app
├── .github/workflows/
│   └── android-build.yml           # CI/CD
└── README.md
```

---

## 🎨 Diseño UI/UX

### Paleta de Colores
```kotlin
Primary:   #FF6B9D (Rosa fuerte)
Secondary: #C73866 (Rosa oscuro)
Background:#FFF0F5 (Rosa claro)
Text:      #2D2D2D (Gris oscuro)
Accent:    #FFD93D (Amarillo)
```

### Fuentes
- Títulos: Poppins Bold
- Texto: Roboto Regular

### Animaciones
- Transición entre preguntas: Slide horizontal
- Botones: Ripple effect
- Aparición: Fade in + Scale + Translation

---

## 🔄 CI/CD - GitHub Actions

El proyecto incluye workflow automático que:

- ✅ Compila en cada push a `main` y `develop`
- ✅ Ejecuta tests automáticos
- ✅ Genera APK de release
- ✅ Crea releases automáticos con versiones
- ✅ Sube artefactos descargables

Archivo: `.github/workflows/android-build.yml`

---

## 🗃️ Base de Datos de Preguntas

### Distribución (120 preguntas totales)

| Categoría | Con Pareja | Soltero | Universal | Total |
|-----------|------------|---------|-----------|-------|
| 🔥 Calientes | 10 | 10 | - | 20 |
| 🤔 Interesantes | 10 | 10 | - | 20 |
| 😄 Divertidas | - | - | 20 | 20 |
| 🌶️ Atrevidas | 10 | 10 | - | 20 |
| 💑 Románticas | 10 | 10 | - | 20 |
| 🎭 Confesiones | - | - | 20 | 20 |

Cada pregunta tiene nivel de intensidad (1-3) para filtrado.

---

## ⚙️ CI/CD — Compilación automática del APK con GitHub Actions

Este repositorio compila la aplicación Android (Kotlin) **automáticamente** en GitHub
mediante el workflow [`.github/workflows/android-build.yml`](.github/workflows/android-build.yml).

### 🔄 ¿Cuándo se ejecuta?

| Evento | Resultado |
|---|---|
| Push a `main` o `develop` | Compila + tests + lint + artefactos |
| Pull Request a `main` | Validación (build + tests + lint) |
| **Push a `main`** | Además crea un **Release** `v1.0.N` con el APK |
| Manual (`workflow_dispatch`) | Botón "Run workflow" en la pestaña **Actions** |

### 🏗️ ¿Qué hace el pipeline?

1. **`build` job** (ubuntu-latest + JDK 17 + Gradle 8.4):
   - `./gradlew build` → compila el proyecto
   - `./gradlew test` → tests unitarios
   - `./gradlew assembleDebug` → **APK instalable** (firmada con keystore debug)
   - `./gradlew assembleRelease` → APK release (sin firmar, lista para tu keystore)
   - Sube ambos APK como **Artifacts** (disponibles 30 días)
   - En `main`: crea **GitHub Release** automático con el APK debug adjunto
2. **`lint` job**: análisis estático de código y reporte HTML en Artifacts

### 📥 ¿Dónde descargar el APK compilado?

- **Artifacts:** pestaña **Actions** → selecciona el *run* → sección *Artifacts*
  - `preguntas-calientes-debug (instalable)` → instálala directo en tu móvil ✅
  - `preguntas-calientes-release-unsigned` → requiere firma propia
- **Releases:** pestaña **Releases** (solo cuando el push es a `main`)

### ▶️ Compilar manualmente en GitHub

1. Ve a **Actions** → **Android CI/CD (Kotlin)**
2. Pulsa **Run workflow** → elige la rama → **Run workflow**
3. Cuando termine (✔ verde), descarga el APK desde *Artifacts*

### 🛠️ Compilar en local

```bash
# Requisitos: JDK 17 y Android SDK (o Android Studio)
chmod +x gradlew

./gradlew assembleDebug     # APK instalable -> app/build/outputs/apk/debug/
./gradlew assembleRelease   # Release      -> app/build/outputs/apk/release/
./gradlew test              # Tests unitarios
./gradlew lint              # Análisis lint
```

### 🔐 Nota sobre la firma del APK

El APK **debug** se firma con el *debug keystore* automático de Android: es instalable
y sirve para pruebas y distribución informal. Para publicar en **Google Play** necesitas
generar tu propio keystore y configurar `signingConfigs` en `app/build.gradle.kts`.

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Haz fork del proyecto
2. Crea una rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

© 2024 Studio Lexair - Todos los derechos reservados

**Desarrollado por Airien Yolexis Rojas Roque**

Este proyecto es de código abierto para fines educativos. Si usas este código, por favor da créditos al autor original.

---

## 🎯 Roadmap / Futuras Mejoras

- [ ] Sistema de puntuación
- [ ] Modo "Verdad o Reto"
- [ ] Compartir preguntas personalizadas
- [ ] Temas oscuro/claro
- [ ] Sonidos y efectos
- [ ] Base de datos SQLite para preguntas custom
- [ ] Modo multijugador online

---

## 📞 Contacto

**Airien Yolexis Rojas Roque**  
Studio Lexair

> Hecho con ❤️ para compartir con amigos

---

### 🌟 ¡Si te gusta el proyecto, dale una estrella! ⭐
