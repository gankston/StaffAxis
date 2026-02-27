plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.registro.empleados"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.registro.empleados"
        minSdk = 26
        targetSdk = 34
        
        // ============================================
        // VERSIÓN DE LA APLICACIÓN
        // ============================================
        // IMPORTANTE: Antes de cada nueva versión, SIEMPRE:
        // 1. OBLIGATORIO: Incrementar versionCode (+1 o más)
        //    - El versionCode DEBE ser mayor que la versión anterior
        //    - Android usa esto para determinar si hay una actualización disponible
        // 2. RECOMENDADO: Actualizar versionName (ej: "1.4" -> "1.5")
        //    - Es la versión visible para el usuario
        // 3. El APK se firmará automáticamente con la misma keystore configurada
        // ============================================
        versionCode = 13
        versionName = "1.7.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Aumentar heap size para Apache POI
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    // ============================================
    // CONFIGURACIÓN DE FIRMA (SIGNING)
    // ============================================
    // IMPORTANTE: SIEMPRE usar la misma keystore para mantener la misma firma
    // en todas las compilaciones. Esto es crítico para:
    // - Actualizaciones de la app (misma firma = actualización permitida)
    // - Mantener la identidad de la app en el dispositivo
    // - Compatibilidad con el sistema de actualizaciones internas
    // ============================================
    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("USERPROFILE") + "\\Desktop\\staffaxis-release.keystore"
            val keystoreFile = file(keystorePath)
            
            // Validar que el keystore existe
            if (!keystoreFile.exists()) {
                throw GradleException(
                    "ERROR: El keystore no se encuentra en: $keystorePath\n" +
                    "Asegúrate de que el archivo 'staffaxis-release.keystore' esté en tu Escritorio."
                )
            }
            
            storeFile = keystoreFile
            storePassword = "staffaxis123"
            keyAlias = "staffaxis"
            keyPassword = "staffaxis123"
        }
    }

    buildTypes {
        debug {
            // Debug sin minify/shrink para builds rápidas y depuración estable
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // SIEMPRE usar la misma firma para release
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    
    // Configuración para KSP
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    // eliminar duplicados: ya configurado arriba
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // JExcelApi para Excel (compatible con Android)
    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")
    
    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    
    // Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Hilt WorkManager
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")
    
    // WindowSizeClass for responsive design
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
