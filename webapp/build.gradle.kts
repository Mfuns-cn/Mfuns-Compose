import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.agp.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt)
}

val getVersionCode = {
    try {
        val out = ByteArrayOutputStream()
        exec {
            commandLine("git", "rev-list", "--count", "HEAD")
            standardOutput = out
        }
        Integer.parseInt(out.toString().trim())
    } catch (e: Exception) {
        1
    }
}

val getVersionName = {
    try {
        val out = ByteArrayOutputStream()
        exec {
            commandLine("git", "describe", "--tags", "--dirty")
            standardOutput = out
        }
        val outStr = out.toString()
        outStr.trim()
    } catch (e: Exception) {
        "0.0.1"
    }
}

val gitVersionCode = getVersionCode()
val gitVersionName = getVersionName()

android {
    namespace = "cn.mfuns.webapp"

    compileSdk = 33

    defaultConfig {
        minSdk = 25
        targetSdk = 33

        versionCode = gitVersionCode
        versionName = gitVersionName

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources {
            excludes += "META-INF/*"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-beta03"
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    coreLibraryDesugaring(libs.android.desugar)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle)

    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.lifecycle)
    implementation(libs.androidx.compose.navigation)

    implementation(libs.google.android.material)

    implementation(libs.android.volley)

    implementation(libs.okhttp.okhttp)

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.github.chrisbanes.photoview)
    implementation(libs.github.castorflex.smoothprogressbar)

    api(libs.tencent.tbs)

    implementation(libs.ilharper.str4j.android)
}
