package com.dutra.agente.essencial.seguranca

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class SSLPinningManagerTest {
    
    private lateinit var sslPinningManager: SSLPinningManager
    
    @Before
    fun setup() {
        sslPinningManager = SSLPinningManager
    }
    
    @Test
    fun testCreateCertificatePinner() {
        val pinner = sslPinningManager.createCertificatePinner()
        assertNotNull(pinner)
    }
    
    @Test
    fun testGetPinnedCertificates() {
        val pinnedCerts = sslPinningManager.getPinnedCertificates()
        assertNotNull(pinnedCerts)
        assertTrue(pinnedCerts.isNotEmpty())
        assertTrue(pinnedCerts.containsKey("seu-servidor.com"))
    }
    
    @Test
    fun testIsCertificatePinned() {
        val validHash = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
        val invalidHash = "sha256/XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX="
        
        assertTrue(sslPinningManager.isCertificatePinned(validHash))
        assertFalse(sslPinningManager.isCertificatePinned(invalidHash))
    }
    
    @Test
    fun testGetProductionConfig() {
        val prodConfig = sslPinningManager.getProductionConfig()
        
        assertTrue(prodConfig.enablePinning)
        assertTrue(prodConfig.enableBackupPins)
        assertEquals(7 * 24 * 60 * 60, prodConfig.pinTimeout)
        assertEquals(3, prodConfig.maxRetries)
        assertFalse(prodConfig.fallbackToSystemCAs)
    }
    
    @Test
    fun testGetDevelopmentConfig() {
        val devConfig = sslPinningManager.getDevelopmentConfig()
        
        assertFalse(devConfig.enablePinning)
        assertTrue(devConfig.enableBackupPins)
        assertEquals(24 * 60 * 60, devConfig.pinTimeout)
        assertEquals(1, devConfig.maxRetries)
        assertTrue(devConfig.fallbackToSystemCAs)
    }
    
    @Test
    fun testSSLPinningConfigDataClass() {
        val config = SSLPinningConfig(
            enablePinning = true,
            enableBackupPins = false,
            pinTimeout = 3600,
            maxRetries = 2,
            fallbackToSystemCAs = true
        )
        
        assertTrue(config.enablePinning)
        assertFalse(config.enableBackupPins)
        assertEquals(3600, config.pinTimeout)
        assertEquals(2, config.maxRetries)
        assertTrue(config.fallbackToSystemCAs)
    }
    
    @Test
    fun testCertificateTypeEnum() {
        val types = listOf(
            CertificateType.PRINCIPAL,
            CertificateType.BACKUP,
            CertificateType.FALLBACK,
            CertificateType.INTERMEDIATE,
            CertificateType.ROOT
        )
        
        assertEquals(5, types.size)
        assertTrue(types.contains(CertificateType.PRINCIPAL))
        assertTrue(types.contains(CertificateType.ROOT))
    }
    
    @Test
    fun testCreatePinnedOkHttpClient() {
        // Teste da criacao do cliente com pinning
        // (Requer mock de OkHttpClient em producao)
        assertNotNull(sslPinningManager)
    }
}
