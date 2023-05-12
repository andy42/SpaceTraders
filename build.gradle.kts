import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("kapt") version kotlinVersion
}

group = "com.jaehl.spaceTraders"
version = "1.0-SNAPSHOT"

val coroutinesVersion = "1.3.6"
val daggerVersion by extra("2.46")
val compose_version = "1.4.0"

repositories {
    google()
    mavenCentral()
    //maven { url = uri("https://jitpack.io") }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kapt {
    generateStubs = true
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation("com.google.dagger:dagger-compiler:$daggerVersion")
                configurations.get("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "com.google.dagger",
                        "dagger-compiler",
                        "$daggerVersion"
                    )
                )
                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")

                implementation ("org.jsoup:jsoup:1.8.3")
                implementation ("com.google.code.gson:gson:2.8.9")
                implementation("com.arkivanov.decompose:decompose:1.0.0")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0")

                implementation("com.google.code.gson:gson:2.10.1")

                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

                implementation("com.squareup.retrofit2:retrofit:2.9.0")
                implementation("com.squareup.retrofit2:converter-gson:2.9.0")
                implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.jaehl.spaceTraders.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SpaceTraders"
            packageVersion = "1.0.0"
        }
    }
}
