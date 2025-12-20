package com.dutra.agente.sync

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dutra.agente.dados.modelos.SyncOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data class representing a queued sync operation for offline-first architecture
 * Stores operations that need to be synced when connection is restored
 */
@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val operationType: String, // CREATE, UPDATE, DELETE
    val entityType: String, // Usuario, Chat, Interacao
    val entityId: String,
    val payload: String, // JSON serialized data
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val status: String = "PENDING" // PENDING, SYNCING, SYNCED, FAILED
)

/**
 * DAO for SyncQueue database operations
 */
@Dao
interface SyncQueueDao {
    @Insert
    suspend fun insert(item: SyncQueueItem)

    @Insert
    suspend fun insertAll(items: List<SyncQueueItem>)

    @Update
    suspend fun update(item: SyncQueueItem)

    @Delete
    suspend fun delete(item: SyncQueueItem)

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY timestamp ASC")
    suspend fun getPendingOperations(): List<SyncQueueItem>

    @Query("SELECT * FROM sync_queue WHERE entityId = :entityId AND status = 'PENDING'")
    suspend fun getPendingOperationsByEntity(entityId: String): List<SyncQueueItem>

    @Query("SELECT * FROM sync_queue WHERE status = 'FAILED' AND retryCount < maxRetries ORDER BY timestamp ASC")
    suspend fun getFailedOperationsForRetry(): List<SyncQueueItem>

    @Query("SELECT * FROM sync_queue WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getOperationsByStatus(status: String): List<SyncQueueItem>

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED' AND timestamp < datetime('now', '-7 days')")
    suspend fun clearOldSyncedOperations()

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    suspend fun getPendingOperationCount(): Int

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
}

/**
 * Room Database for SyncQueue management
 */
@Database(entities = [SyncQueueItem::class], version = 1, exportSchema = false)
abstract class SyncQueueDatabase : RoomDatabase() {
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var instance: SyncQueueDatabase? = null

        fun getInstance(context: Context): SyncQueueDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SyncQueueDatabase::class.java,
                    "sync_queue_database"
                ).build().also { instance = it }
            }
        }
    }
}

/**
 * SyncQueue Manager - Manages offline sync operations with retry logic and conflict resolution
 * Implements queue-based sync pattern for offline-first architecture
 */
class SyncQueue(
    private val context: Context,
    private val dao: SyncQueueDao
) {
    /**
     * Enqueues a new sync operation
     */
    suspend fun enqueue(
        operationType: String,
        entityType: String,
        entityId: String,
        payload: String
    ) = withContext(Dispatchers.IO) {
        val item = SyncQueueItem(
            operationType = operationType,
            entityType = entityType,
            entityId = entityId,
            payload = payload,
            status = "PENDING"
        )
        dao.insert(item)
    }

    /**
     * Gets all pending operations in order
     */
    suspend fun getPendingOperations(): List<SyncQueueItem> = withContext(Dispatchers.IO) {
        dao.getPendingOperations()
    }

    /**
     * Marks an operation as synced
     */
    suspend fun markAsSynced(item: SyncQueueItem) = withContext(Dispatchers.IO) {
        val synced = item.copy(status = "SYNCED")
        dao.update(synced)
    }

    /**
     * Marks an operation as failed and increments retry count
     */
    suspend fun markAsFailed(item: SyncQueueItem) = withContext(Dispatchers.IO) {
        val failed = item.copy(
            status = if (item.retryCount + 1 >= item.maxRetries) "FAILED" else "PENDING",
            retryCount = item.retryCount + 1
        )
        dao.update(failed)
    }

    /**
     * Gets operations that can be retried
     */
    suspend fun getFailedOperationsForRetry(): List<SyncQueueItem> = withContext(Dispatchers.IO) {
        dao.getFailedOperationsForRetry()
    }

    /**
     * Gets pending operations count
     */
    suspend fun getPendingCount(): Int = withContext(Dispatchers.IO) {
        dao.getPendingOperationCount()
    }

    /**
     * Removes an operation from queue
     */
    suspend fun remove(item: SyncQueueItem) = withContext(Dispatchers.IO) {
        dao.delete(item)
    }

    /**
     * Clears old synced operations (cleanup)
     */
    suspend fun cleanup() = withContext(Dispatchers.IO) {
        dao.clearOldSyncedOperations()
    }

    /**
     * Checks if entity has pending operations
     */
    suspend fun hasPendingOperations(entityId: String): Boolean = withContext(Dispatchers.IO) {
        dao.getPendingOperationsByEntity(entityId).isNotEmpty()
    }

    /**
     * Gets operations by status
     */
    suspend fun getOperationsByStatus(status: String): List<SyncQueueItem> = withContext(Dispatchers.IO) {
        dao.getOperationsByStatus(status)
    }
}
