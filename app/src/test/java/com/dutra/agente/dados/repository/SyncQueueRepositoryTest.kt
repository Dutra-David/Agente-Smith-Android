package com.dutra.agente.dados.repository

import androidx.room.Room
import android.content.Context
import com.dutra.agente.dados.local.AgentDatabase
import com.dutra.agente.dados.local.dao.SyncQueueDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking

class SyncQueueRepositoryTest {
    
    private lateinit var database: AgentDatabase
    private lateinit var syncQueueDao: SyncQueueDao
    private lateinit var repository: SyncQueueRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            AgentDatabase::class.java
        ).allowMainThreadQueries().build()
        
        syncQueueDao = database.syncQueueDao()
        repository = SyncQueueRepository(syncQueueDao)
    }
    
    @After
    fun cleanup() {
        database.close()
    }
    
    @Test
    fun testEnqueueOperacao() = runBlocking {
        val operacao = SyncOperacao(
            id = 1,
            tipo = "MESSAGE_SEND",
            dados = "test_message",
            timestamp = System.currentTimeMillis(),
            tentativas = 0,
            status = "PENDING"
        )
        
        repository.enqueue(operacao)
        val operacoes = repository.getPendingOperations()
        
        assertEquals(1, operacoes.size)
        assertEquals("MESSAGE_SEND", operacoes[0].tipo)
    }
    
    @Test
    fun testDequeueOperacao() = runBlocking {
        val operacao = SyncOperacao(
            id = 1,
            tipo = "MESSAGE_SEND",
            dados = "test_message",
            timestamp = System.currentTimeMillis(),
            tentativas = 0,
            status = "PENDING"
        )
        repository.enqueue(operacao)
        
        val resultado = repository.dequeue()
        
        assertNotNull(resultado)
        assertEquals("MESSAGE_SEND", resultado?.tipo)
    }
    
    @Test
    fun testRetryLogic() = runBlocking {
        val operacao = SyncOperacao(
            id = 1,
            tipo = "MESSAGE_SEND",
            dados = "test_message",
            timestamp = System.currentTimeMillis(),
            tentativas = 0,
            status = "PENDING"
        )
        repository.enqueue(operacao)
        
        repository.markAsRetry(1)
        val tentativas = repository.getTentativas(1)
        
        assertEquals(1, tentativas)
    }
    
    @Test
    fun testPersistenciaEmBD() = runBlocking {
        val operacao1 = SyncOperacao(
            id = 1,
            tipo = "MESSAGE_SEND",
            dados = "message_1",
            timestamp = System.currentTimeMillis(),
            tentativas = 0,
            status = "PENDING"
        )
        val operacao2 = SyncOperacao(
            id = 2,
            tipo = "MESSAGE_DELETE",
            dados = "message_2",
            timestamp = System.currentTimeMillis(),
            tentativas = 0,
            status = "PENDING"
        )
        
        repository.enqueue(operacao1)
        repository.enqueue(operacao2)
        val pendentes = repository.getPendingOperations()
        
        assertEquals(2, pendentes.size)
        assertTrue(pendentes.any { it.tipo == "MESSAGE_SEND" })
        assertTrue(pendentes.any { it.tipo == "MESSAGE_DELETE" })
    }
}
