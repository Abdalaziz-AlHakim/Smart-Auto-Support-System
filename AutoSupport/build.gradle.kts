import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.compose") version "1.6.11"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

group   = "com.autosupport"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.animation)
}

sourceSets {
    main {
        // Java domain classes live directly under AutoSupport/ (package root = ".")
        // Exclude old Swing GUI + Swing Main — replaced by Kotlin Compose entry point
        java { srcDirs("."); exclude("out/**", "gui/**", "Main.java") }
        // Kotlin Compose UI lives in src/main/kotlin/
        kotlin { srcDirs("src/main/kotlin") }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"          // src/main/kotlin/main.kt
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName    = "AutoSupport"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
