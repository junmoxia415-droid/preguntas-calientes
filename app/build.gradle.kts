/**
 * Preguntas Calientes - Party Game
 * Desarrollado por: Airien Yolexis Rojas Roque
 * Studio: Studio Lexair
 * Versión: 1.0.0
 */
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.studiolexair.preguntascalientes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.studiolexair.preguntascalientes"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // 📦 Los APK que se generan llevan el NOMBRE DEL JUEGO (no "app-debug.apk")
    // Debug (distribución):  Preguntas-Calientes-v2.0.0.apk
    // Release (sin firmar):  Preguntas-Calientes-v2.0.0-release-unsigned.apk
    applicationVariants.all {
        val variantName = name
        val version = versionName
        outputs.all {
            val apkOutput = this as? com.android.build.gradle.internal.api.ApkVariantOutputImpl
            apkOutput?.outputFileName =
                if (variantName == "release") "Preguntas-Calientes-v$version-release-unsigned.apk"
                else "Preguntas-Calientes-v$version.apk"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Room - Base de datos local (preguntas custom, estadísticas, logros, partidas)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Gson - Serialización de partidas guardadas
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
