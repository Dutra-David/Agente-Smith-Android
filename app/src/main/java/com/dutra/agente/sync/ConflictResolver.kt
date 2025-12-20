package com.dutra.agente.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data class for representing conflicted data versions
 */
data class ConflictedVersion(
    val localVersion: String,
    val remoteVersion: String,
    val localTimestamp: String,
    val remoteTimestamp: String,
    val entityId: String,
    val entityType: String
)

/**
 * Enum for conflict resolution strategies
 */
enum class ConflictResolutionStrategy {
    LAST_WRITE_WINS,      // Most recent timestamp wins
    LOCAL_PRIORITY,        // Keep local version
    REMOTE_PRIORITY,       // Keep remote version
    MERGE,                 // Attempt to merge both versions
    MANUAL_INTERVENTION    // Flag for user intervention
}

/**
 * Result of conflict resolution
 */
data class ConflictResolutionResult(
    val resolved: Boolean,
    val winningVersion: String?,
    val strategy: ConflictResolutionStrategy,
    val mergedData: Map<String, Any>? = null,
    val requiresManualIntervention: Boolean = false,
    val message: String
)

/**
 * ConflictResolver - Handles data conflicts in offline-first sync scenarios
 * Implements multiple strategies for conflict resolution
 */
class ConflictResolver {

    /**
     * Resolves conflicts between local and remote data versions
     */
    suspend fun resolve(
        conflict: ConflictedVersion,
        strategy: ConflictResolutionStrategy = ConflictResolutionStrategy.LAST_WRITE_WINS
    ): ConflictResolutionResult = withContext(Dispatchers.Default) {
        when (strategy) {
            ConflictResolutionStrategy.LAST_WRITE_WINS -> resolveByLastWriteWins(conflict)
            ConflictResolutionStrategy.LOCAL_PRIORITY -> resolveWithLocalPriority(conflict)
            ConflictResolutionStrategy.REMOTE_PRIORITY -> resolveWithRemotePriority(conflict)
            ConflictResolutionStrategy.MERGE -> resolveByMerge(conflict)
            ConflictResolutionStrategy.MANUAL_INTERVENTION -> flagForManualIntervention(conflict)
        }
    }

    /**
     * Last Write Wins strategy - uses timestamps to determine winner
     */
    private fun resolveByLastWriteWins(conflict: ConflictedVersion): ConflictResolutionResult {
        return try {
            val localTime = parseTimestamp(conflict.localTimestamp)
            val remoteTime = parseTimestamp(conflict.remoteTimestamp)

            val winningVersion = if (localTime.isAfter(remoteTime)) {
                conflict.localVersion
            } else if (remoteTime.isAfter(localTime)) {
                conflict.remoteVersion
            } else {
                // Same timestamp, prefer local
                conflict.localVersion
            }

            ConflictResolutionResult(
                resolved = true,
                winningVersion = winningVersion,
                strategy = ConflictResolutionStrategy.LAST_WRITE_WINS,
                message = "Resolved using Last Write Wins strategy. Winner: ${if (winningVersion == conflict.localVersion) "Local" else "Remote"}"
            )
        } catch (e: Exception) {
            ConflictResolutionResult(
                resolved = false,
                strategy = ConflictResolutionStrategy.LAST_WRITE_WINS,
                requiresManualIntervention = true,
                message = "Error in Last Write Wins resolution: ${e.message}"
            )
        }
    }

    /**
     * Local Priority strategy - always keeps local version
     */
    private fun resolveWithLocalPriority(conflict: ConflictedVersion): ConflictResolutionResult {
        return ConflictResolutionResult(
            resolved = true,
            winningVersion = conflict.localVersion,
            strategy = ConflictResolutionStrategy.LOCAL_PRIORITY,
            message = "Resolved using Local Priority strategy"
        )
    }

    /**
     * Remote Priority strategy - always keeps remote version
     */
    private fun resolveWithRemotePriority(conflict: ConflictedVersion): ConflictResolutionResult {
        return ConflictResolutionResult(
            resolved = true,
            winningVersion = conflict.remoteVersion,
            strategy = ConflictResolutionStrategy.REMOTE_PRIORITY,
            message = "Resolved using Remote Priority strategy"
        )
    }

    /**
     * Merge strategy - attempts to intelligently merge both versions
     * Supports JSON-like merge for object data
     */
    private fun resolveByMerge(conflict: ConflictedVersion): ConflictResolutionResult {
        return try {
            // Simple string-based merge: combine both versions
            val mergedData = mutableMapOf<String, Any>()
            
            // Parse JSON objects if possible
            try {
                val localMap = parseJsonToMap(conflict.localVersion)
                val remoteMap = parseJsonToMap(conflict.remoteVersion)
                
                // Merge maps: newer values take precedence
                mergedData.putAll(localMap)
                remoteMap.forEach { (key, value) ->
                    if (key !in mergedData || isNewerValue(value, mergedData[key])) {
                        mergedData[key] = value
                    }
                }
            } catch (e: Exception) {
                // If JSON parsing fails, create merge record
                mergedData["_local_version"] = conflict.localVersion
                mergedData["_remote_version"] = conflict.remoteVersion
                mergedData["_conflict_flag"] = true
            }

            ConflictResolutionResult(
                resolved = true,
                winningVersion = null,
                strategy = ConflictResolutionStrategy.MERGE,
                mergedData = mergedData,
                message = "Merged local and remote versions"
            )
        } catch (e: Exception) {
            ConflictResolutionResult(
                resolved = false,
                strategy = ConflictResolutionStrategy.MERGE,
                requiresManualIntervention = true,
                message = "Error during merge: ${e.message}"
            )
        }
    }

    /**
     * Manual Intervention strategy - flags conflict for user review
     */
    private fun flagForManualIntervention(conflict: ConflictedVersion): ConflictResolutionResult {
        return ConflictResolutionResult(
            resolved = false,
            strategy = ConflictResolutionStrategy.MANUAL_INTERVENTION,
            requiresManualIntervention = true,
            message = "Conflict flagged for manual intervention. Entity: ${conflict.entityType}[${conflict.entityId}]"
        )
    }

    /**
     * Batch resolve multiple conflicts
     */
    suspend fun resolveBatch(
        conflicts: List<ConflictedVersion>,
        strategy: ConflictResolutionStrategy = ConflictResolutionStrategy.LAST_WRITE_WINS
    ): List<ConflictResolutionResult> = withContext(Dispatchers.Default) {
        conflicts.map { resolve(it, strategy) }
    }

    /**
     * Detect conflicts between two data versions
     */
    fun detectConflict(localData: String, remoteData: String): Boolean {
        return localData != remoteData
    }

    /**
     * Helper: Parse timestamp string to LocalDateTime
     */
    private fun parseTimestamp(timestamp: String): LocalDateTime {
        return try {
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }

    /**
     * Helper: Parse JSON string to Map
     */
    private fun parseJsonToMap(jsonString: String): Map<String, Any> {
        // Simple implementation - can be enhanced with real JSON parser
        return try {
            // This is a simplified version - use org.json or kotlinx.serialization in production
            mapOf("value" to jsonString)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Helper: Determine if one value is newer than another
     */
    private fun isNewerValue(newValue: Any?, oldValue: Any?): Boolean {
        return newValue?.toString()?.length ?: 0 > oldValue?.toString()?.length ?: 0
    }
}
