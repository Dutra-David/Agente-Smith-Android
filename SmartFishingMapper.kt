package com.dutra.agente.domain.maps

import com.dutra.agente.domain.models.FishingSpot
import com.dutra.agente.domain.models.EnvironmentalCondition
import com.dutra.agente.domain.repositories.FishingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.*

/**
 * SMART FISHING MAPPER - Inteligência Geoespacial
 * 
 * Mapeamento de:
 * - Hotspots de pesca (Heat maps)
 * - Rotas otimizadas
 * - Predição de migração de peixes
 * - Áreas com melhor historíco
 */

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

data class LatLngBounds(
    val southwest: LatLng,
    val northeast: LatLng
)

data class FishingSpot(
    val id: String,
    val name: String,
    val location: LatLng,
    val type: SpotType, // rio, lago, oceano
    val successCount: Int,
    val lastVisit: Long,
    val averageWeight: Double,
    val confidenceLevel: Double, // 0-100
    val season: String? = null
)

enum class SpotType {
    RIVER, LAKE, OCEAN, POND, STREAM
}

data class HeatmapData(
    val intensity: Map<String, Double>, // coordenada -> intensidade (0-100)
    val topHotspots: List<FishingSpot>,
    val generatedAt: Long,
    val boundingBox: LatLngBounds
)

data class Route(
    val waypoints: List<LatLng>,
    val distance: Double, // km
    val estimatedTime: Int, // minutos
    val difficulty: RouteDifficulty,
    val accessibility: String,
    val spotSequence: List<FishingSpot>
)

enum class RouteDifficulty {
    EASY, MODERATE, HARD, VERY_HARD
}

data class MigrationForecast(
    val season: String,
    val fishSpecies: String,
    val expectedLocations: List<LatLng>,
    val timing: String,
    val confidence: Double
)

class SmartFishingMapper(
    private val fishingRepository: FishingRepository,
    private val gpsIntegration: GPSEnvironmentalIntegration
) {
    
    private val _heatmapData = MutableStateFlow<HeatmapData?>(null)
    val heatmapData: StateFlow<HeatmapData?> = _heatmapData
    
    /**
     * Gera HEAT MAP de melhores spots históricos
     * Cores: Verde (melhor) -> Amarelo -> Vermelho (pior)
     */
    suspend fun generateFishingHeatmap(bounds: LatLngBounds): HeatmapData {
        // 1. Buscar histórico de pesca
        val history = fishingRepository.getUserFishingHistory(limit = 500)
        
        // 2. Criar grade de intensidade
        val intensityMap = mutableMapOf<String, Double>()
        
        history.forEach { session ->
            val gridKey = latLngToGridKey(session.location, bounds)
            val currentIntensity = intensityMap[gridKey] ?: 0.0
            
            // Aumenta intensidade baseado em sucesso
            val success = session.catchCount.toDouble()
            val newIntensity = currentIntensity + (success * 10)
            intensityMap[gridKey] = newIntensity.coerceAtMost(100.0)
        }
        
        // 3. Encontrar TOP hotspots
        val topHotspots = extractTopHotspots(intensityMap, bounds)
        
        // 4. Normalizar intensidades
        val maxIntensity = intensityMap.values.maxOrNull() ?: 1.0
        intensityMap.forEach { (key, value) ->
            intensityMap[key] = (value / maxIntensity) * 100
        }
        
        val heatmap = HeatmapData(
            intensity = intensityMap,
            topHotspots = topHotspots,
            generatedAt = System.currentTimeMillis(),
            boundingBox = bounds
        )
        
        _heatmapData.emit(heatmap)
        return heatmap
    }
    
    /**
     * Otimizar rota: Passar por vários spots minimizando distância
     * (Travelling Salesman Problem simplificado)
     */
    fun optimizeRoute(
        startPoint: LatLng,
        fishingSpots: List<FishingSpot>
    ): Route {
        if (fishingSpots.isEmpty()) {
            return Route(
                waypoints = listOf(startPoint),
                distance = 0.0,
                estimatedTime = 0,
                difficulty = RouteDifficulty.EASY,
                accessibility = "Apenas ponto de partida",
                spotSequence = emptyList()
            )
        }
        
        // Algoritmo ganancioso: próximo spot mais próximo
        val waypoints = mutableListOf(startPoint)
        val visitedSpots = mutableListOf<FishingSpot>()
        var currentLocation = startPoint
        var totalDistance = 0.0
        
        var remainingSpots = fishingSpots.sortedByDescending { it.confidenceLevel }
        
        while (remainingSpots.isNotEmpty()) {
            // Encontrar próximo spot mais próximo
            val nearest = remainingSpots.minByOrNull { 
                calculateDistance(currentLocation, it.location) 
            } ?: break
            
            val distance = calculateDistance(currentLocation, nearest.location)
            totalDistance += distance
            
            waypoints.add(nearest.location)
            visitedSpots.add(nearest)
            currentLocation = nearest.location
            remainingSpots = remainingSpots.filter { it.id != nearest.id }
        }
        
        val estimatedTime = (totalDistance / 50 * 60).toInt() // Assume 50km/h médio
        val difficulty = when {
            totalDistance < 10 -> RouteDifficulty.EASY
            totalDistance < 30 -> RouteDifficulty.MODERATE
            totalDistance < 100 -> RouteDifficulty.HARD
            else -> RouteDifficulty.VERY_HARD
        }
        
        return Route(
            waypoints = waypoints,
            distance = totalDistance,
            estimatedTime = estimatedTime,
            difficulty = difficulty,
            accessibility = "${visitedSpots.size} spots em ${"%.1f".format(totalDistance)}km",
            spotSequence = visitedSpots
        )
    }
    
    /**
     * Predição de Migração de Peixes
     * Basado em dados históricos sazonais
     */
    suspend fun predictFishMigration(season: String): MigrationForecast {
        val history = fishingRepository.getUserFishingHistory(limit = 1000)
        
        // Filtrar por temporada
        val seasonalData = history.filter { it.season == season }
        
        // Encontrar locarções mais frequentes nesta temporada
        val locationFrequency = seasonalData
            .groupBy { "${it.location.latitude},${it.location.longitude}" }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
        
        val expectedLocations = locationFrequency
            .take(5)
            .map { (coord, _) ->
                val parts = coord.split(",")
                LatLng(parts[0].toDouble(), parts[1].toDouble())
            }
        
        // Determinar espécie mais comum nesta temporada
        val fishSpecies = seasonalData
            .groupingBy { it.fishSpecies }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: "Desconhecida"
        
        return MigrationForecast(
            season = season,
            fishSpecies = fishSpecies,
            expectedLocations = expectedLocations,
            timing = generateTimingInfo(seasonalData),
            confidence = (locationFrequency.size.toDouble() / history.size * 100).coerceAtMost(100.0)
        )
    }
    
    /**
     * Funções Auxiliares
     */
    
    private fun latLngToGridKey(location: LatLng, bounds: LatLngBounds): String {
        val latGrid = ((location.latitude - bounds.southwest.latitude) / 0.01).toInt()
        val lngGrid = ((location.longitude - bounds.southwest.longitude) / 0.01).toInt()
        return "$latGrid,$lngGrid"
    }
    
    private fun extractTopHotspots(
        intensityMap: Map<String, Double>,
        bounds: LatLngBounds
    ): List<FishingSpot> {
        return intensityMap
            .filter { it.value > 50 } // Apenas alta intensidade
            .map { (gridKey, intensity) ->
                val parts = gridKey.split(",")
                val lat = bounds.southwest.latitude + (parts[0].toInt() * 0.01)
                val lng = bounds.southwest.longitude + (parts[1].toInt() * 0.01)
                
                FishingSpot(
                    id = gridKey,
                    name = "Hotspot $gridKey",
                    location = LatLng(lat, lng),
                    type = SpotType.LAKE,
                    successCount = (intensity / 10).toInt(),
                    lastVisit = System.currentTimeMillis(),
                    averageWeight = 2.5,
                    confidenceLevel = intensity
                )
            }
            .sortedByDescending { it.confidenceLevel }
            .take(10)
    }
    
    private fun calculateDistance(from: LatLng, to: LatLng): Double {
        // Fórmula Haversine (distância entre dois pontos geográficos)
        val R = 6371.0 // Raio da Terra em km
        val lat1 = from.latitude * PI / 180
        val lat2 = to.latitude * PI / 180
        val dLat = (to.latitude - from.latitude) * PI / 180
        val dLng = (to.longitude - from.longitude) * PI / 180
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return R * c
    }
    
    private fun generateTimingInfo(seasonalData: List<FishingSession>): String {
        val hours = seasonalData.map { it.startTime.hour }
        val mostCommonHour = hours.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        return mostCommonHour?.let { "Melhor entre ${it}h e ${it + 2}h" } ?: "Variável"
    }
}
