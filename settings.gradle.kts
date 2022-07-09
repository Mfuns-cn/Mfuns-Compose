rootProject.name = "Mfuns-WebApp"

include(":app")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("android-gradle", "com.android.tools.build", "gradle").version("7.2.1")
            library("kotlin-gradle", "org.jetbrains.kotlin", "kotlin-gradle-plugin").version("1.6.21")
            library("spotless", "com.diffplug.spotless", "spotless-plugin-gradle").version("6.7.2")

            library("android-desugar", "com.android.tools", "desugar_jdk_libs").version("1.1.5")

            library("dagger-hilt-gradle", "com.google.dagger", "hilt-android-gradle-plugin").version("2.42")
            library("dagger-hilt-android", "com.google.dagger", "hilt-android").version("2.42")
            library("dagger-hilt-compiler", "com.google.dagger", "hilt-android-compiler").version("2.42")

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

            library("google-android-material", "com.google.android.material", "material").version("1.4.0")

            library("android-volley", "com.android.volley", "volley").version("1.2.1")

            library("github-chrisbanes-photoview", "com.github.chrisbanes", "PhotoView").version("2.3.0")
            library("github-castorflex-smoothprogressbar", "com.github.castorflex.smoothprogressbar", "library-circular").version("1.3.0")

            library("tencent-tbs", "com.tencent.tbs", "tbssdk").version("44181")

            library("ilharper-str4j-android", "com.ilharper.str4j", "android").version("0.1.0")
        }
    }
}
