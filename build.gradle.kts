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
extval composeVersion = "1.5.0"
extval material3Version = "1.1.1"
extval hiltVersion = "2.46"
extval retrofitVersion = "2.9.0"
extval okhttpVersion = "4.11.0"
extval kotlinxSerializationVersion = "1.6.0"
extval roomVersion = "2.5.2"
extval coroutinesVersion = "1.7.1"
