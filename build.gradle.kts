allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.ilharper.com") }
    }
}

plugins {
    alias(libs.plugins.spotless)
}

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
}
