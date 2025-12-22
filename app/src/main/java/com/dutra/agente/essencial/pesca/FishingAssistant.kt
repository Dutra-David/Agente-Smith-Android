package com.dutra.agente.essencial.pesca

import android.content.Context
import com.dutra.agente.essencial.localizacao.GPSLocationManager
import com.dutra.agente.essencial.localizacao.LocationData

data class FishingSpot(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val type: String, // Rio, Lago, Oceano
    val bestSeason: String
)

data class FishingRecommendation(
    val spot: FishingSpot,
    val distance: Float,
    val bearing: Float,
    val conditions: FishingWeatherData,
    val recommendedSpecies: List<String>,
    val techniques: List<String>,
    val bestHours: String,
    val riskLevel: String
)

/**
 * Assistente de Pesca Inteligente
 * Integra GPS, condicoes meteorologicas e dados de pesca
 */
class FishingAssistant(private val context: Context) {

    private val gpsManager = GPSLocationManager(context)
    private val weatherManager = FishingWeatherManager()

    // Base de dados de locais de pesca populares
    private val knownSpots = listOf(
        FishingSpot(
            name = "Rio Doce - Ponte Nova",
            latitude = -20.39,
            longitude = -42.91,
            description = "Excelente para dourados e tilapías",
            type = "Rio",
            bestSeason = "Primavera/Verão"
        ),
        FishingSpot(
            name = "Represa de Furnas",
            latitude = -20.97,
            longitude = -45.77,
            description = "Área grande com varias espécies",
            type = "Represa",
            bestSeason = "Outono/Inverno"
        ),
        FishingSpot(
            name = "Praia de Itapema",
            latitude = -27.10,
            longitude = -48.60,
            description = "Pesca maritima de robalo e corvina",
            type = "Oceano",
            bestSeason = "O ano todo"
        ),
        FishingSpot(
            name = "Lagoa Mirim",
            latitude = -31.30,
            longitude = -54.85,
            description = "Agua doce com trairas e piranhas",
            type = "Lagoa",
            bestSeason = "Verão"
        )
    )

    /**
     * Obtem recomendacoes de pesca para localizacao atual
     */
    fun getFishingRecommendations(
        maxResults: Int = 3
    ): List<FishingRecommendation> {
        val currentLocation = gpsManager.getLastLocation() ?: return emptyList()
        return findNearbyFishingSpots(currentLocation, maxResults)
    }

    /**
     * Encontra spots de pesca proximos e retorna recomendacoes
     */
    private fun findNearbyFishingSpots(
        currentLocation: LocationData,
        maxResults: Int
    ): List<FishingRecommendation> {
        return knownSpots
            .map { spot ->
                val distance = gpsManager.calculateDistance(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    spot.latitude,
                    spot.longitude
                )

                val bearing = gpsManager.calculateBearing(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    spot.latitude,
                    spot.longitude
                )

                val conditions = weatherManager.analyzeFishingConditions(
                    spot.latitude,
                    spot.longitude
                )

                val species = weatherManager.recommendFishSpecies(
                    spot.latitude,
                    spot.longitude
                )

                val techniques = getTechniquesBySpotType(spot.type)
                val riskLevel = evaluateRiskLevel(conditions, distance)

                FishingRecommendation(
                    spot = spot,
                    distance = distance,
                    bearing = bearing,
                    conditions = conditions,
                    recommendedSpecies = species,
                    techniques = techniques,
                    bestHours = conditions.bestFishingTime,
                    riskLevel = riskLevel
                )
            }
            .sortedBy { it.distance }
            .take(maxResults)
    }

    /**
     * Retorna tecnicas de pesca baseadas no tipo de corpo de agua
     */
    private fun getTechniquesBySpotType(spotType: String): List<String> {
        return when (spotType.lowercase()) {
            "rio" -> listOf(
                "Arremesso com isco vivo",
                "Meia agua",
                "Fundo com carretilha",
                "Fly casting"
            )
            "represa", "lagoa" -> listOf(
                "Pesca a float",
                "Meia agua",
                "Fundo com parada",
                "Arremesso com plug"
            )
            "oceano" -> listOf(
                "Arremesso de praia",
                "Pesca com carretilha",
                "Fundo com ledger",
                "Nado de superficie"
            )
            else -> listOf("Varias tecnicas podem funcionar")
        }
    }

    /**
     * Avalia o nível de risco da expedicao
     */
    private fun evaluateRiskLevel(
        conditions: FishingWeatherData,
        distance: Float
    ): String {
        val windRisk = when (conditions.wind.quality) {
            "Excelente", "Boa" -> 0
            "Regular" -> 1
            else -> 2
        }

        val distanceRisk = when {
            distance < 5000 -> 0 // < 5km
            distance < 20000 -> 1 // < 20km
            else -> 2 // > 20km
        }

        val warnings = conditions.warnings.size

        val totalRisk = windRisk + distanceRisk + warnings
        return when {
            totalRisk >= 4 -> "ALTO"
            totalRisk >= 2 -> "MEDIO"
            else -> "BAIXO"
        }
    }

    /**
     * Gera relatorio detalhado para um spot especifico
     */
    fun generateDetailedReport(spot: FishingSpot): String {
        val conditions = weatherManager.analyzeFishingConditions(spot.latitude, spot.longitude)
        val species = weatherManager.recommendFishSpecies(spot.latitude, spot.longitude)
        val techniques = getTechniquesBySpotType(spot.type)

        return """
            |=== RELATORIO DE PESCA ===
            |Local: ${spot.name}
            |Tipo: ${spot.type}
            |Descricao: ${spot.description}
            |Melhor Estacao: ${spot.bestSeason}
            |
            |=== CONDICOES METEOROLOGICAS ===
            |Vento: ${conditions.wind.directionName} (${conditions.wind.speedKmh} km/h)
            |Rajadas: ${conditions.wind.gustsKmh} km/h
            |Qualidade Vento: ${conditions.wind.quality}
            |
            |=== DADOS DE MARE ===
            |Altura: ${conditions.tide.heightMeters}m
            |Tipo: ${conditions.tide.type}
            |Intensidade: ${conditions.tide.intensity}
            |Proximo Cambio: ${conditions.tide.nextChangeHours}h
            |
            |=== FASE DA LUA ===
            |Fase: ${conditions.moonPhase.phaseName}
            |Iluminacao: ${conditions.moonPhase.percentIlluminated.toInt()}%
            |Qualidade Pesca: ${conditions.moonPhase.fishingQuality}
            |Recomendacao: ${conditions.moonPhase.recommendation}
            |
            |=== AVALIACAO GERAL ===
            |Qualidade Geral: ${conditions.overallQuality}
            |Melhor Horario: ${conditions.bestFishingTime}
            |Especies Recomendadas: ${species.joinToString(", ")}
            |Tecnicas: ${techniques.joinToString(", ")}
            |
            |=== AVISOS ===
            |${if (conditions.warnings.isNotEmpty()) conditions.warnings.joinToString("\n|") else "Nenhum aviso"}
        """.trimMargin()
    }

    /**
     * Inicia rastreamento de localizacao para atualizacoes em tempo real
     */
    fun startLocationTracking(callback: (FishingRecommendation?) -> Unit) {
        if (!gpsManager.hasLocationPermission()) {
            return
        }

        gpsManager.startLocationUpdates { location ->
            val recommendations = findNearbyFishingSpots(location, 1)
            callback(recommendations.firstOrNull())
        }
    }

    /**
     * Para rastreamento de localizacao
     */
    fun stopLocationTracking() {
        gpsManager.stopLocationUpdates()
    }

    /**
     * Adiciona um novo spot de pesca customizado
     */
    fun addCustomFishingSpot(spot: FishingSpot) {
        // Em producao, salvar em banco de dados local (Room)
        // Por enquanto, apenas em memoria durante sessao
    }
}
