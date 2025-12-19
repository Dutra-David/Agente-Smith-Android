package com.dutra.agente

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * AgentSmithApplication - Main application class
 * 
 * Sets up:
 * - Hilt dependency injection
 * - Timber logging
 * - App-level initialization
 */
@HiltAndroidApp
class AgentSmithApplication : Application() {
  
  override fun onCreate() {
    super.onCreate()
    
    // Initialize Timber logging
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    
    Timber.d("AgentSmithApplication initialized successfully")
  }
}
