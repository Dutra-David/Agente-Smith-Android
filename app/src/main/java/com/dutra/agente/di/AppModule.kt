package com.dutra.agente.di

import com.dutra.agente.network.RetrofitClient
import com.dutra.agente.network.ApiService
import dagger.Module            
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule - Hilt dependency injection module
 *
 * Provides singleton instances of:
 * - ApiService for network calls
 *
 * Note: MessageRepository is provided via @Inject constructor, so it doesn't need to be here.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provide ApiService instance (singleton)
     */
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }
}
