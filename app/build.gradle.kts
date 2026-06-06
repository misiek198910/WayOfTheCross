import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)

}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "droga_krzyzowa.droga_krzyzowa"
    compileSdk = 36

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "droga_krzyzowa.droga_krzyzowa"
        minSdk = 28
        targetSdk = 36
        versionCode = 43
        versionName = "3.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Domyślna wartość, aby manifest nie sypał błędami
        manifestPlaceholders["adMobAppId"] = ""
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("MYAPP_RELEASE_STORE_FILE", "brak"))
            storePassword = localProperties.getProperty("MYAPP_RELEASE_STORE_PASSWORD", "")
            keyAlias = localProperties.getProperty("MYAPP_RELEASE_KEY_ALIAS", "")
            keyPassword = localProperties.getProperty("MYAPP_RELEASE_KEY_PASSWORD", "")
        }
    }

    buildTypes {
        getByName("debug") {
            // Testowe AdMob App ID
            manifestPlaceholders["adMobAppId"] = "ca-app-pub-3940256099942544~3347511713"

            // Testowe Unit ID dla bannerów
            buildConfigField("String", "AD_BANNER_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "AD_START_UNIT_ID", "\"ca-app-pub-3940256099942544/3419835294\"")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            val adMobAppId = localProperties.getProperty("AD_APP_ID", "")
            val bannerId = localProperties.getProperty("AD_BANNER_ID", "")
            val adStartId = localProperties.getProperty("AD_START_UNIT_ID", "")

            manifestPlaceholders["adMobAppId"] = adMobAppId
            buildConfigField("String", "AD_BANNER_ID", "\"$bannerId\"")
            buildConfigField("String", "AD_START_UNIT_ID", "\"$adStartId\"")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {

    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.runtime.livedata)

    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.common.java8)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.glide.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.ads)
    implementation(libs.androidx.billing.ktx)
}