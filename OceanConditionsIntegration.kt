package com.dutra.agente.fishing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.*
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Integração de Condições Oceânicas para Pesca
 * Monitora onda, corrente, visibilidade subaquática e temperatura
 */

data class OceanConditions(
    val waveHeight: Double,
    val wavePeriod: Double,
    val waveDirection: String,
    val swellHeight: Double,
    val currentSpeed: Double,
    val currentDirection: String,
    val waterClarity: WaterClarity,
    val waterTemperature: Double,
    val salinity: Double,
    val oxygen: Double,
    val phLevel: Double,
    val biomassIndicator: BiomassLevel,
    val fishingQuality: FishingQuality,
    val timestamp: Long = System.currentTimeMillis()
)

enum class WaterClarity {
    CRYSTAL_CLEAR,
    VERY_CLEAR,
    CLEAR,
    MODERATE,
    MURKY,
    VERY_MURKY
}

enum class BiomassLevel {
    VERY_LOW, LOW, MODERATE, HIGH, VERY_HIGH
}

enum class FishingQuality {
    EXCELLENT, GOOD, FAIR, POOR, VERY_POOR
}

data class WaveData(
    val height: Double,
    val period: Double,
    val direction: String,
    val energy: Double
)

data class CurrentData(
    val speed: Double,
    val direction: String,
    val depthAffected: Double
)

data class WaterQuality(
    val temperature: Double,
    val salinity: Double,
    val dissolvedOxygen: Double,
    val pH: Double,
    val turbidity: Double,
    val nutrientLevel: Double
)

class OceanConditionsIntegration {

    private val _oceanConditions = MutableStateFlow<OceanConditions?>(null)
    val oceanConditions: StateFlow<OceanConditions?> = _oceanConditions

    private val _waveData = MutableStateFlow<WaveData?>(null)
    val waveData: StateFlow<WaveData?> = _waveData

    private val _currentData = MutableStateFlow<CurrentData?>(null)
    val currentData: StateFlow<CurrentData?> = _currentData

    private val _waterQuality = MutableStateFlow<WaterQuality?>(null)
    val waterQuality: StateFlow<WaterQuality?> = _waterQuality

    companion object {
        private const val WAVE_FREQUENCY = 12.0
        private const val CURRENT_VARIATION = 0.5
        private const val BASE_WATER_TEMP = 20.0
    }

    suspend fun updateOceanConditions(latitude: Double, longitude: Double) {
        val waves = calculateWaveData(latitude, longitude)
        val current = calculateCurrentData(latitude, longitude)
        val quality = calculateWaterQuality(latitude, longitude)
        val biomass = estimateBiomass(waves, current, quality)
        val fishingQuality = evaluateFishingQuality(waves, current, quality, biomass)

        val conditions = OceanConditions(
            waveHeight = waves.height,
            wavePeriod = waves.period,
            waveDirection = waves.direction,
            swellHeight = waves.energy,
            currentSpeed = current.speed,
            currentDirection = current.direction,
            waterClarity = evaluateWaterClarity(quality.turbidity),
            waterTemperature = quality.temperature,
            salinity = quality.salinity,
            oxygen = quality.dissolvedOxygen,
            phLevel = quality.pH,
            biomassIndicator = biomass,
            fishingQuality = fishingQuality
        )

        _oceanConditions.emit(conditions)
        _waveData.emit(waves)
        _currentData.emit(current)
        _waterQuality.emit(quality)
    }

    private fun calculateWaveData(latitude: Double, longitude: Double): WaveData {
        val baseHeight = 1.5 + (latitude % 5.0) * 0.3
        val variation = sin(System.currentTimeMillis() / 3600000.0 * 2 * PI) * 0.5
        val height = (baseHeight + variation).coerceIn(0.5, 5.0)
        
        val period = 8.0 + (longitude % 10.0) * 0.2
        
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val dirIndex = ((longitude + 180) / 45).toInt() % 8
        val direction = directions[dirIndex]
        
        val energy = height * height * period
        
        return WaveData(
            height = height,
            period = period,
            direction = direction,
            energy = energy
        )
    }

    private fun calculateCurrentData(latitude: Double, longitude: Double): CurrentData {
        val baseCurrent = 0.3 + (latitude % 5.0) * 0.05
        val variation = cos(System.currentTimeMillis() / 7200000.0 * 2 * PI) * 0.1
        val speed = (baseCurrent + variation).coerceIn(0.1, 1.5)
        
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val dirIndex = ((longitude - latitude + 180) / 45).toInt() % 8
        val direction = directions[dirIndex]
        
        val depthAffected = 50.0 + (latitude % 30.0)
        
        return CurrentData(
            speed = speed,
            direction = direction,
            depthAffected = depthAffected
        )
    }

    private fun calculateWaterQuality(latitude: Double, longitude: Double): WaterQuality {
        val seasonalFactor = sin((System.currentTimeMillis() / (1000.0 * 86400.0 * 365.25)) * 2 * PI)
        
        val temperature = BASE_WATER_TEMP + (latitude / 90.0 * 15.0) + (seasonalFactor * 5.0)
        val salinity = 35.0 + (abs(latitude) / 90.0 * 3.0) + (longitude % 10.0) * 0.1
        val oxygen = 8.0 + (abs(latitude) / 90.0 * 2.0) - ((35.0 - salinity) * 0.1)
        val pH = 8.1 + ((longitude % 20.0) / 20.0 * 0.2)
        val turbidity = (((longitude + latitude) % 100.0) / 100.0) * 10.0
        val nutrientLevel = ((latitude % 45.0) / 45.0) * 100.0
        
        return WaterQuality(
            temperature = temperature.coerceIn(5.0, 35.0),
            salinity = salinity.coerceIn(32.0, 38.0),
            dissolvedOxygen = oxygen.coerceIn(0.0, 12.0),
            pH = pH.coerceIn(7.5, 8.5),
            turbidity = turbidity.coerceIn(0.0, 10.0),
            nutrientLevel = nutrientLevel.coerceIn(0.0, 100.0)
        )
    }

    private fun evaluateWaterClarity(turbidity: Double): WaterClarity {
        return when {
            turbidity < 0.5 -> WaterClarity.CRYSTAL_CLEAR
            turbidity < 1.5 -> WaterClarity.VERY_CLEAR
            turbidity < 3.0 -> WaterClarity.CLEAR
            turbidity < 5.0 -> WaterClarity.MODERATE
            turbidity < 7.5 -> WaterClarity.MURKY
            else -> WaterClarity.VERY_MURKY
        }
    }

    private fun estimateBiomass(waves: WaveData, current: CurrentData, quality: WaterQuality): BiomassLevel {
        var score = 50.0
        
        // Fator Ondas
        if (waves.height in 1.0..3.0) score += 20.0 else score -= 10.0
        
        // Fator Corrente
        if (current.speed in 0.2..0.8) score += 15.0 else score -= 10.0
        
        // Fator Temperatura
        if (quality.temperature in 18.0..26.0) score += 20.0
        
        // Fator Oxígênio Dissolvido
        if (quality.dissolvedOxygen > 6.0) score += 15.0
        
        // Fator Nutrientes
        score += (quality.nutrientLevel / 100.0) * 20.0
        
        return when {
            score >= 85 -> BiomassLevel.VERY_HIGH
            score >= 70 -> BiomassLevel.HIGH
            score >= 50 -> BiomassLevel.MODERATE
            score >= 35 -> BiomassLevel.LOW
            else -> BiomassLevel.VERY_LOW
        }
    }

    private fun evaluateFishingQuality(waves: WaveData, current: CurrentData, quality: WaterQuality, biomass: BiomassLevel): FishingQuality {
        var qualityScore = 50.0
        
        // Ondas ideais: 1-3m
        qualityScore += when {
            waves.height in 1.0..3.0 -> 20.0
            waves.height in 0.5..1.0 || waves.height in 3.0..4.0 -> 10.0
            else -> -10.0
        }
        
        // Corrente ideal: 0.2-0.8 m/s
        qualityScore += when {
            current.speed in 0.2..0.8 -> 20.0
            current.speed in 0.1..0.2 || current.speed in 0.8..1.2 -> 10.0
            else -> -10.0
        }
        
        // Temperatura ideal: 18-26°C
        qualityScore += when {
            quality.temperature in 18.0..26.0 -> 15.0
            quality.temperature in 15.0..28.0 -> 8.0
            else -> 0.0
        }
        
        // Oxígênio
        qualityScore += when {
            quality.dissolvedOxygen > 7.0 -> 15.0
            quality.dissolvedOxygen > 5.0 -> 8.0
            else -> 0.0
        }
        
        // Biomassa
        qualityScore += when (biomass) {
            BiomassLevel.VERY_HIGH -> 20.0
            BiomassLevel.HIGH -> 15.0
            BiomassLevel.MODERATE -> 10.0
            BiomassLevel.LOW -> 5.0
            BiomassLevel.VERY_LOW -> 0.0
        }
        
        return when {
            qualityScore >= 90 -> FishingQuality.EXCELLENT
            qualityScore >= 75 -> FishingQuality.GOOD
            qualityScore >= 50 -> FishingQuality.FAIR
            qualityScore >= 30 -> FishingQuality.POOR
            else -> FishingQuality.VERY_POOR
        }
    }

    // Método para obter alertas de segurança
    fun getOceanSafetyAlerts(conditions: OceanConditions): List<String> {
        val alerts = mutableListOf<String>()
        
        if (conditions.waveHeight > 4.5) {
            alerts.add("AVISO: Ondas muito altas (${conditions.waveHeight}m). Risco moderado.")
        }
        
        if (conditions.currentSpeed > 1.2) {
            alerts.add("AVISO: Corrente forte (${conditions.currentSpeed} m/s). Use cautela.")
        }
        
        if (conditions.waterClarity == WaterClarity.VERY_MURKY) {
            alerts.add("PERIGO: Água muito turva. Visibilidade mínima.")
        }
        
        if (conditions.waterTemperature < 10.0) {
            alerts.add("AVISO: Água muito fria. Use proteção térmica.")
        }
        
        if (conditions.oxygen < 4.0) {
            alerts.add("PERIGO: Baixo oxígênio dissolvido. Má qualidade da água.")
        }
        
        return alerts
    }
}
