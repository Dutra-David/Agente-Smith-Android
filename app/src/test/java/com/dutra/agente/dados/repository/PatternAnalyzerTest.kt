package com.dutra.agente.dados.repository

import com.dutra.agente.dominio.analisa.PatternAnalyzer
import com.dutra.agente.dominio.analisa.Pattern
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class PatternAnalyzerTest {
    
    private lateinit var analyzer: PatternAnalyzer
    
    @Before
    fun setup() {
        analyzer = PatternAnalyzer()
    }
    
    @Test
    fun testAnaliseDepadroesSimples() {
        val entrada = listOf(
            "usuario: ola",
            "usuario: ola",
            "usuario: ola",
            "usuario: adeus",
            "usuario: adeus"
        )
        
        val padroes = analyzer.analisar(entrada)
        
        assertNotNull(padroes)
        assertTrue(padroes.isNotEmpty())
        assertTrue(padroes.any { it.padrao.contains("ola") })
    }
    
    @Test
    fun testConfiancaEstatistica() {
        val entrada = listOf(
            "mensagem: importante",
            "mensagem: importante",
            "mensagem: importante",
            "mensagem: importante"
        )
        
        val padroes = analyzer.analisar(entrada)
        
        assertTrue(padroes.isNotEmpty())
        val confianca = padroes[0].confianca
        assertTrue(confianca >= 0.75)
    }
    
    @Test
    fun testCasosExtremos() {
        val padroesVazio = analyzer.analisar(emptyList())
        assertTrue(padroesVazio.isEmpty())
        
        val padroeUnica = analyzer.analisar(listOf("teste: valor"))
        val confianca = padroeUnica.getOrNull(0)?.confianca ?: 0.0
        assertTrue(confianca < 0.5)
    }
    
    @Test
    fun testPadraoMultiplo() {
        val entrada = listOf(
            "tipo: A, valor: 1",
            "tipo: A, valor: 1",
            "tipo: B, valor: 2",
            "tipo: B, valor: 2",
            "tipo: B, valor: 2",
            "tipo: C, valor: 3"
        )
        
        val padroes = analyzer.analisar(entrada)
        
        assertTrue(padroes.size >= 2)
        val padraoMaisFrequente = padroes.maxByOrNull { it.frequencia }
        assertNotNull(padraoMaisFrequente)
        assertTrue(padraoMaisFrequente!!.confianca > 0.4)
    }
}
