plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "mz.co.moseguros.app"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // O flutter_local_notifications (S6-07) usa APIs java.time.
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    defaultConfig {
        applicationId = "mz.co.moseguros.app"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    // Três ambientes. O HTTP sem TLS só é permitido no flavor dev
    // (src/dev/res/xml/network_security_config.xml).
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "MoSeguros Dev")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            resValue("string", "app_name", "MoSeguros Staging")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "MoSeguros")
        }
    }

    buildTypes {
        release {
            // A assinatura das builds de loja é do Sprint 12; por agora usa a chave de debug.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

flutter {
    source = "../.."
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
}

// Push (S6-07 §4): o google-services.json de cada flavor fica em
// src/{dev,staging,prod}/ e fora do Git. Sem nenhum, o plugin não se aplica e a
// app usa o NoopPushService; um flavor sem o seu ficheiro não corre a tarefa.
val firebaseFlavors = listOf("dev", "staging", "prod").filter {
    file("src/$it/google-services.json").exists()
}
if (firebaseFlavors.isNotEmpty()) {
    apply(plugin = "com.google.gms.google-services")
    tasks.configureEach {
        if (name.startsWith("process") && name.endsWith("GoogleServices")) {
            val variant = name.removePrefix("process").removeSuffix("GoogleServices")
            enabled = firebaseFlavors.any { variant.lowercase().startsWith(it) }
        }
    }
}
