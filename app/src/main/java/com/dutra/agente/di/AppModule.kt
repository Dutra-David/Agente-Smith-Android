package com.dutra.agente.di

import com.dutra.agente.network.RetrofitClient
import com.dutra.agente.network.ApiService
import com.dutra.agente.data.repository.MessageRepository
import dagger.Module            
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.dutra.agente.nucleo.tentativa.RetryStrategy
import com.dutra.agente.rede.RateLimitingInterceptor

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
    
    /**
     * Provide RetryStrategy instance (singleton)
     * 
     * Handles exponential backoff with jitter for network operations
     */
    @Provides
    @Singleton
    fun provideRetryStrategy(): RetryStrategy {
        return RetryStrategy(
            maxRetries = 5,
            initialDelayMs = 1000,
            maxDelayMs = 30000,
            backoffMultiplier = 2.0
        )
    }
    
    /**
     * Provide RateLimitingInterceptor (singleton)
     * 
     * Integrates retry logic with OkHttp client
     */
    @Provides
    @Singleton
    fun provideRateLimitingInterceptor(retryStrategy: RetryStrategy): RateLimitingInterceptor {
        return RateLimitingInterceptor(retryStrategy)
    }
}
}
