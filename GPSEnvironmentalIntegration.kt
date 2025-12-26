package com.dutra.agente.fishing

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.*

/**
 * GPS Environmental Integration para Agente de Pesca
 * Integra dados de localização, vento, maré, fase da lua e clima
 */

data class EnvironmentalData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val windSpeed: Double = 0.0,
    val windDirection: String = "",
    val tideLevel: Double = 0.0,
    val tideType: TideType = TideType.LOW,
    val moonPhase: MoonPhase = MoonPhase.NEW_MOON,
    val temperature: Double = 0.0,
    val humidity: Double = 0.0,
    val pressureMb: Double = 0.0,
    val visibility: Double = 0.0,
    val waterTemp: Double = 0.0,
    val bestFishingScore: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TideType {
    LOW, HIGH, RISING, FALLING
}

enum class MoonPhase {
    NEW_MOON, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS,
    FULL_MOON, WANING_GIBBOUS, LAST_QUARTER, WANING_CRESCENT
}

data class WindData(
    val speed: Double,
    val direction: String,
    val gustSpeed: Double = 0.0,
    val favorable: Boolean = true
)

data class TideData(
    val level: Double,
    val type: TideType,
    val nextChange: Long,
    val nextChangeType: TideType
)

data class MoonData(
    val phase: MoonPhase,
    val illumination: Double,
    val age: Int,
    val distance: Double
)

class GPSEnvironmentalIntegration(
    private val context: Context
) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val _environmentalData = MutableStateFlow<EnvironmentalData?>(null)
    val environmentalData: StateFlow<EnvironmentalData?> = _environmentalData
    
    private val _windData = MutableStateFlow<WindData?>(null)
    val windData: StateFlow<WindData?> = _windData
    
    private val _tideData = MutableStateFlow<TideData?>(null)
    val tideData: StateFlow<TideData?> = _tideData
    
    private val _moonData = MutableStateFlow<MoonData?>(null)
    val moonData: StateFlow<MoonData?> = _moonData

    // Constantes Astronômicas
    companion object {
        private const val REFERENCE_EPOCH = 2451545.0 // JD 2000.0
        private const val MEAN_LUNAR_MONTH = 29.53058867
        private const val TIDE_AMPLITUDE = 2.5
        private const val MEAN_TIDE_LEVEL = 0.0
    }

    // Obter localização atual via GPS
    suspend fun updateCurrentLocation() {
        try {
            val location = fusedLocationClient.lastLocation.result
            location?.let {
                calculateEnvironmentalFactors(it.latitude, it.longitude)
            }
        } catch (e: SecurityException) {
            // Permissão de localização não concedida
            e.printStackTrace()
        }
    }

    // Calcular todos os fatores ambientais
    private suspend fun calculateEnvironmentalFactors(lat: Double, lon: Double) {
        val wind = calculateWindData(lat, lon)
        val tide = calculateTideData(lat, lon)
        val moon = calculateMoonData()
        
        val score = calculateFishingScore(wind, tide, moon)
        
        val data = EnvironmentalData(
            latitude = lat,
            longitude = lon,
            windSpeed = wind.speed,
            windDirection = wind.direction,
            tideLevel = tide.level,
            tideType = tide.type,
            moonPhase = moon.phase,
            temperature = fetchWeatherData(lat, lon).temperature,
            humidity = fetchWeatherData(lat, lon).humidity,
            pressureMb = fetchWeatherData(lat, lon).pressure,
            visibility = fetchWeatherData(lat, lon).visibility,
            waterTemp = estimateWaterTemperature(lat, lon),
            bestFishingScore = score
        )
        
        _environmentalData.emit(data)
        _windData.emit(wind)
        _tideData.emit(tide)
        _moonData.emit(moon)
    }

    // Cálculo de Vento
    private fun calculateWindData(lat: Double, lon: Double): WindData {
        // Simulação realista de vento baseada em localização
        val baseWind = 15.0 + (lat % 10.0)
        val gusts = baseWind * 1.3
        
        val windDirections = arrayOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val directionIndex = ((lon + 180) / 45).toInt() % 8
        val direction = windDirections[directionIndex]
        
        val favorable = windDirections[directionIndex] in arrayOf("O", "SO", "NO")
        
        return WindData(
            speed = baseWind,
            direction = direction,
            gustSpeed = gusts,
            favorable = favorable
        )
    }

    // Cálculo de Maré
    private fun calculateTideData(lat: Double, lon: Double): TideData {
        val now = System.currentTimeMillis()
        val hourOfDay = (now / 3600000L) % 24
        
        // Ciclo semi-diurno (2 marés altas por dia)
        val tidePosition = (hourOfDay % 12.4).toDouble() / 12.4
        val tideLevel = TIDE_AMPLITUDE * sin(tidePosition * 2 * PI)
        
        val tideType = when {
            tidePosition < 0.25 -> TideType.RISING
            tidePosition < 0.5 -> TideType.HIGH
            tidePosition < 0.75 -> TideType.FALLING
            else -> TideType.LOW
        }
        
        val nextChangeTime = ((12.4 - (hourOfDay % 12.4)) * 3600000L).toLong()
        val nextChangeType = when (tideType) {
            TideType.LOW, TideType.RISING -> TideType.HIGH
            TideType.HIGH, TideType.FALLING -> TideType.LOW
        }
        
        return TideData(
            level = tideLevel + MEAN_TIDE_LEVEL,
            type = tideType,
            nextChange = now + nextChangeTime,
            nextChangeType = nextChangeType
        )
    }

    // Cálculo de Fase da Lua (Algoritmo Astronômico)
    private fun calculateMoonData(): MoonData {
        val now = System.currentTimeMillis() / 1000.0
        val jd = (now / 86400.0) + 2440587.5
        
        // Cálculo da idade lunar
        val age = ((jd - REFERENCE_EPOCH) % MEAN_LUNAR_MONTH).toInt()
        val illumination = (1.0 + cos((age * 360.0 / MEAN_LUNAR_MONTH) * PI / 180.0)) / 2.0 * 100.0
        
        val moonPhase = when (age) {
            in 0..3 -> MoonPhase.NEW_MOON
            in 4..10 -> MoonPhase.WAXING_CRESCENT
            in 11..12 -> MoonPhase.FIRST_QUARTER
            in 13..19 -> MoonPhase.WAXING_GIBBOUS
            in 20..23 -> MoonPhase.FULL_MOON
            in 24..27 -> MoonPhase.WANING_GIBBOUS
            in 28..29 -> MoonPhase.LAST_QUARTER
            else -> MoonPhase.WANING_CRESCENT
        }
        
        val distance = 384400.0 + 21000.0 * cos((age * 360.0 / MEAN_LUNAR_MONTH) * PI / 180.0)
        
        return MoonData(
            phase = moonPhase,
            illumination = illumination,
            age = age,
            distance = distance
        )
    }

    // Buscar dados de clima (integração com API de clima)
    private suspend fun fetchWeatherData(lat: Double, lon: Double): WeatherResponse {
        return try {
            // Simulação de dados de clima
            WeatherResponse(
                temperature = 22.0 + (lat % 5.0),
                humidity = 65.0 + (lon % 20.0),
                pressure = 1013.0 + (lat % 10.0),
                visibility = 10.0
            )
        } catch (e: Exception) {
            WeatherResponse(22.0, 65.0, 1013.0, 10.0)
        }
    }

    // Estimar temperatura da água
    private fun estimateWaterTemperature(lat: Double, lon: Double): Double {
        val baseTemp = 18.0
        val latEffect = (lat / 90.0) * 12.0 // Varia com latitude
        val lonEffect = (lon % 180.0) / 180.0 * 4.0
        val seasonalEffect = 5.0 * sin((System.currentTimeMillis() / (1000.0 * 86400.0 * 365.25)) * 2 * PI)
        
        return baseTemp + latEffect + lonEffect + seasonalEffect
    }

    // Calcular score de pesca (1-100)
    private fun calculateFishingScore(wind: WindData, tide: TideData, moon: MoonData): Int {
        var score = 50
        
        // Fator Vento
        score += if (wind.favorable) 15 else -10
        score += when {
            wind.speed < 5 -> -10
            wind.speed in 5.0..15.0 -> 15
            wind.speed > 25 -> -15
            else -> 0
        }
        
        // Fator Maré
        score += when (tide.type) {
            TideType.RISING, TideType.FALLING -> 15
            TideType.HIGH, TideType.LOW -> -10
        }
        
        // Fator Lua
        score += when (moon.phase) {
            MoonPhase.FULL_MOON, MoonPhase.NEW_MOON -> 20
            MoonPhase.FIRST_QUARTER, MoonPhase.LAST_QUARTER -> 10
            else -> 0
        }
        
        return score.coerceIn(1, 100)
    }

    // Recomendaçåo de local de pesca
    suspend fun recommendFishingLocation(currentLat: Double, currentLon: Double): FishingRecommendation {
        val data = _environmentalData.value ?: return FishingRecommendation("", 0.0, 0.0, 0)
        
        val recommendation = when {
            data.bestFishingScore >= 80 -> "EXCELENTE - Condições ideais para pesca!"
            data.bestFishingScore >= 60 -> "BOM - Condições favoráveis"
            data.bestFishingScore >= 40 -> "MÉDIO - Condições aceitáveis"
            else -> "RUIM - Aguarde melhores condições"
        }
        
        return FishingRecommendation(
            recommendation = recommendation,
            bestLatitude = currentLat,
            bestLongitude = currentLon,
            score = data.bestFishingScore
        )
    }
}

data class WeatherResponse(
    val temperature: Double,
    val humidity: Double,
    val pressure: Double,
    val visibility: Double
)

data class FishingRecommendation(
    val recommendation: String,
    val bestLatitude: Double,
    val bestLongitude: Double,
    val score: Int
)
