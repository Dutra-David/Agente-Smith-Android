// Top-level build file for Agente-Smith-Android

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    kotlin("android") version "1.9.10" apply false
    kotlin("kapt") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.46" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
