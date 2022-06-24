plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
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
    compileSdkVersion(31)

    defaultConfig {
        applicationId("cn.mfuns.webapp")
        minSdkVersion(25)
        targetSdkVersion(25)
        versionCode = gitVersionCode
        versionName = gitVersionName

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enabled = true
    }

    dataBinding {
        enabled = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("com.android.volley:volley:1.2.1")

    implementation("com.google.dagger:hilt-android:2.42")
    kapt("com.google.dagger:hilt-compiler:2.42")

    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.castorflex.smoothprogressbar:library-circular:1.3.0")
    api("com.tencent.tbs:tbssdk:44181")
    implementation("com.ilharper:droidup:0.1.5")
}
