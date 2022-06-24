buildscript {
    ext.kotlin_version = "1.7.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = "https://www.jitpack.io" }
    }
}

apply(plugin = "com.diffplug.spotless")
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    format("misc") {
        target("**/*.md", "**/*.editorconfig", "**/*.gitignore", "**/*.gitattributes")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")
        ktlint().setUseExperimental(true)
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlinGradle {
        target("**/*.kts")
        ktlint().setUseExperimental(true)
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    format("xml") {
        target("**/*.xml")
        prettier(
            mapOf(
                "@prettier/plugin-xml" to "*"
            )
        ).config(
            mapOf(
                "tabWidth" to 4,
                "bracketSameLine" to true
            )
        )
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}
