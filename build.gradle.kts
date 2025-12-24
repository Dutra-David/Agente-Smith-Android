// Top-level build file for Agente-Smith-Android
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    kotlin("android") version "1.9.10" apply false
    kotlin("kapt") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.46" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Dependency versions
val composeVersion by extra("1.5.0")
val material3Version by extra("1.1.1")
val hiltVersion by extra("2.46")
val retrofitVersion by extra("2.9.0")
val okhttpVersion by extra("4.11.0")
val kotlinxSerializationVersion by extra("1.6.0")
val roomVersion by extra("2.5.2")
val coroutinesVersion by extra("1.7.1")