package com.dutra.agente.essencial.seguranca

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.util.concurrent.TimeUnit

/**
 * SSLPinningManager - Gerencia SSL Certificate Pinning
 * 
 * Implementa pinning de certificados SSL para prevenir ataques MITM (Man-in-the-Middle)
 * usando OkHttp CertificatePinner com hashing SHA-256 de chaves publicas.
 */
object SSLPinningManager {
    
    // Hashes SHA-256 das chaves publicas esperadas
    // Formatos: "sha256/[HASH_AQUI]"
    private val expectedPins = listOf(
        "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", // Certificado Principal
        "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=", // Certificado Backup
        "sha256/CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC="   // Certificado Fallback
    )
    
    /**
     * Cria CertificatePinner com os hashes das chaves publicas
     * 
     * Para obter os hashes corretos:
     * ```
     * openssl s_client -connect seu-servidor.com:443 < /dev/null | \
     * openssl x509 -noout -pubkey | \
     * openssl pkey -pubin -outform DER | \
     * openssl dgst -sha256 -binary | \
     * openssl enc -base64
     * ```
     */
    fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("seu-servidor.com", *expectedPins.toTypedArray())
            .add("api.seu-servidor.com", *expectedPins.toTypedArray())
            // Adicione aqui outros dominios que sua app acessa
            .build()
    }
    
    /**
     * Configura OkHttpClient com SSL Pinning habilitado
     * 
     * @param baseOkHttpClient Cliente OkHttp base para ser configurado
     * @return OkHttpClient configurado com SSL Pinning
     */
    fun createPinnedOkHttpClient(baseOkHttpClient: OkHttpClient): OkHttpClient {
        return baseOkHttpClient.newBuilder()
            .certificatePinner(createCertificatePinner())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Retorna lista de certificados pinados para configuracao
     * 
     * @return Map com dominio -> lista de hashes SHA-256
     */
    fun getPinnedCertificates(): Map<String, List<String>> {
        return mapOf(
            "seu-servidor.com" to expectedPins,
            "api.seu-servidor.com" to expectedPins
        )
    }
    
    /**
     * Valida se um certificado esta na lista de pins permitidos
     * 
     * @param certificateHash Hash SHA-256 do certificado
     * @return true se o certificado esta pinado, false caso contrario
     */
    fun isCertificatePinned(certificateHash: String): Boolean {
        return expectedPins.contains(certificateHash)
    }
    
    /**
     * Retorna configuracao recomendada para producao
     */
    fun getProductionConfig(): SSLPinningConfig {
        return SSLPinningConfig(
            enablePinning = true,
            enableBackupPins = true,
            pinTimeout = 7 * 24 * 60 * 60, // 7 dias em segundos
            maxRetries = 3,
            fallbackToSystemCAs = false
        )
    }
    
    /**
     * Retorna configuracao para desenvolvimento (com pins mais permissivos)
     */
    fun getDevelopmentConfig(): SSLPinningConfig {
        return SSLPinningConfig(
            enablePinning = false, // Desabilitado em dev para facilitar testes
            enableBackupPins = true,
            pinTimeout = 24 * 60 * 60, // 1 dia em segundos
            maxRetries = 1,
            fallbackToSystemCAs = true
        )
    }
}

/**
 * Configuracao de SSL Pinning
 * 
 * @property enablePinning Se SSL Pinning esta habilitado
 * @property enableBackupPins Se certificados de backup sao permitidos
 * @property pinTimeout Tempo de expiracao dos pins em segundos
 * @property maxRetries Numero maximo de tentativas apos falha de pin
 * @property fallbackToSystemCAs Se deve usar CAs do sistema como fallback
 */
data class SSLPinningConfig(
    val enablePinning: Boolean = true,
    val enableBackupPins: Boolean = true,
    val pinTimeout: Long = 7 * 24 * 60 * 60, // 7 dias
    val maxRetries: Int = 3,
    val fallbackToSystemCAs: Boolean = false
)

/**
 * Enum para diferentes tipos de certificados
 */
enum class CertificateType {
    PRINCIPAL,      // Certificado principal da aplicacao
    BACKUP,         // Certificado de backup
    FALLBACK,       // Certificado de fallback
    INTERMEDIATE,   // Certificado intermediario
    ROOT            // Certificado raiz
}
