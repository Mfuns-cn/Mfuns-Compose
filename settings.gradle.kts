rootProject.name = "Mfuns-WebApp"

include(":webapp")

pluginManagement {
    val versions = mapOf(
        "agp" to "8.0.1",
        "kotlin" to "1.6.21",
        "compose" to "1.1.0",
        "dagger" to "2.42",
        "spotless" to "6.7.2"
    )

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        id("com.android.application").version(versions["agp"])
        id("com.android.library").version(versions["agp"])

        kotlin("android").version(versions["kotlin"])
        kotlin("kapt").version(versions["kotlin"])

        id("com.google.dagger.hilt.android").version(versions["dagger"])

        id("com.diffplug.spotless").version(versions["spotless"])
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("dagger", "2.42")
            version("okhttp", "4.10.0")

            library("android-desugar", "com.android.tools", "desugar_jdk_libs").version("1.1.5")

            library("dagger-hilt-android", "com.google.dagger", "hilt-android").versionRef("dagger")
            library("dagger-hilt-compiler", "com.google.dagger", "hilt-android-compiler").versionRef("dagger")

            library("androidx-appcompat", "androidx.appcompat", "appcompat").version("1.4.2")
            library("androidx-preference", "androidx.preference", "preference-ktx").version("1.2.0")
            library("androidx-constraintlayout", "androidx.constraintlayout", "constraintlayout").version("2.1.4")
            library("androidx-lifecycle", "androidx.lifecycle", "lifecycle-runtime-ktx").version("2.3.1")

            library("androidx-compose-activity", "androidx.activity", "activity-compose").version("1.4.0")
            library("androidx-compose-material", "androidx.compose.material", "material").version("1.1.1")
            library("androidx-compose-animation", "androidx.compose.animation", "animation").version("1.1.1")
            library("androidx-compose-ui", "androidx.compose.ui", "ui-tooling").version("1.1.1")
            library("androidx-compose-lifecycle", "androidx.lifecycle", "lifecycle-viewmodel-compose").version("2.4.1")
            library("androidx-compose-navigation", "androidx.navigation", "navigation-compose").version("2.5.0")

            library("google-android-material", "com.google.android.material", "material").version("1.6.1")

            library("android-volley", "com.android.volley", "volley").version("1.2.1")

            library("okhttp-okhttp", "com.squareup.okhttp3", "okhttp").versionRef("okhttp")

            library("github-chrisbanes-photoview", "com.github.chrisbanes", "PhotoView").version("2.3.0")
            library("github-castorflex-smoothprogressbar", "com.github.castorflex.smoothprogressbar", "library-circular").version("1.3.0")

            library("tencent-tbs", "com.tencent.tbs", "tbssdk").version("54002-beta")

            library("ilharper-str4j-android", "com.ilharper.str4j", "android").version("0.1.5")
        }
    }
}
