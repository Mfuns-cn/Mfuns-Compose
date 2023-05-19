rootProject.name = "Mfuns-Compose"

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
