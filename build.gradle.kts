buildscript {
    ext.kotlin_version = "1.7.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.spotless)
        classpath(libs.dagger.hilt.gradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = "https://maven.ilharper.com" }
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
