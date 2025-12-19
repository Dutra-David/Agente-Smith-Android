package com.dutra.agente.di

import com.dutra.agente.network.RetrofitClient
import com.dutra.agente.network.ApiService
import com.dutra.agente.data.repository.MessageRepository
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
 * - MessageRepository for data access
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

    /**
     * Provide MessageRepository instance (singleton)
     * 
     * The ApiService is injected as a dependency for repository access
     */
    @Provides
    @Singleton
    fun provideMessageRepository(apiService: ApiService): MessageRepository {
        return MessageRepository(apiService)
    }
}
