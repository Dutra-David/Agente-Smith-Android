package com.dutra.agente.essencial.pesca

import java.util.Calendar
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.cos

data class WindData(
    val speedKmh: Float,
    val directionDegrees: Float, // 0-360
    val directionName: String,
    val gustsKmh: Float = 0f,
    val quality: String // Excelente, Boa, Regular, Ruim
)

data class TideData(
    val heightMeters: Float,
    val type: String, // Subida, Descida, Preamar, Baixamar
    val intensity: String, // Alta, Media, Baixa
    val nextChangeHours: Float
)

data class MoonPhase(
    val phaseName: String,
    val percentIlluminated: Float, // 0-100
    val dayInCycle: Int, // 0-29
    val fishingQuality: String, // Excelente, Boa, Regular, Ruim
    val recommendation: String
)

data class FishingWeatherData(
    val wind: WindData,
    val tide: TideData,
    val moonPhase: MoonPhase,
    val overallQuality: String,
    val bestFishingTime: String,
    val warnings: List<String> = emptyList()
)

/**
 * Gerenciador de condicoes meteorologicas e astronomicas para pesca
 * Fornece dados de vento, mare e fase da lua
 */
class FishingWeatherManager {

    /**
     * Calcula dados de vento (simulacao com base em historico)
     * Em producao, integrar com API de clima (OpenWeatherMap, INPE)
     */
    fun getWindData(latitude: Double, longitude: Double): WindData {
        // Simulacao para demonstracao
        // Em producao: chamar API de clima
        val windSpeed = (5..25).random().toFloat()
        val windDirection = (0..359).random().toFloat()
        val directionName = getWindDirectionName(windDirection)
        val gusts = windSpeed + (0..10).random()
        val quality = when {
            windSpeed < 5 -> "Ruim"
            windSpeed < 12 -> "Regular"
            windSpeed < 20 -> "Boa"
            else -> "Excelente"
        }

        return WindData(
            speedKmh = windSpeed,
            directionDegrees = windDirection,
            directionName = directionName,
            gustsKmh = gusts,
            quality = quality
        )
    }

    /**
     * Converte graus de vento em direcao
     */
    private fun getWindDirectionName(degrees: Float): String {
        return when {
            degrees < 22.5 || degrees >= 337.5 -> "N (Norte)"
            degrees < 67.5 -> "NE (Nordeste)"
            degrees < 112.5 -> "E (Leste)"
            degrees < 157.5 -> "SE (Sudeste)"
            degrees < 202.5 -> "S (Sul)"
            degrees < 247.5 -> "SO (Sudoeste)"
            degrees < 292.5 -> "O (Oeste)"
            else -> "NO (Noroeste)"
        }
    }

    /**
     * Calcula dados de mare
     * Usa modelo simplificado com ciclo de 12h 25min
     */
    fun getTideData(latitude: Double, longitude: Double): TideData {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteOfDay = calendar.get(Calendar.MINUTE)
        val totalMinutes = hourOfDay * 60 + minuteOfDay

        // Ciclo de mare simplificado (em minutos: ~745min = 12h25min)
        val tidePosition = (totalMinutes % 745).toFloat()
        val tideHeight = 2f + (1.5f * sin((tidePosition / 745f) * 2 * Math.PI)).toFloat()

        val tideType = when {
            tidePosition < 186 -> "Subida"    // Enchente
            tidePosition < 372 -> "Preamar"   // Maré alta
            tidePosition < 558 -> "Descida"   // Vazante
            else -> "Baixamar"                // Maré baixa
        }

        val intensity = when (tideHeight) {
            in 2.5..3.5 -> "Alta"
            in 1.5..2.5 -> "Media"
            else -> "Baixa"
        }

        val nextChangeHours = ((372 - tidePosition) / 60).coerceAtLeast(0.5f)

        return TideData(
            heightMeters = tideHeight,
            type = tideType,
            intensity = intensity,
            nextChangeHours = nextChangeHours
        )
    }

    /**
     * Calcula fase da lua
     * Usa formula astronomica simplificada baseada na data
     */
    fun getMoonPhase(): MoonPhase {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Formula de Gauss para calcular fase da lua
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3

        val jdn = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        val dayInCycle = ((jdn - 2451550) % 29.53).toInt() // 29.53 dias = ciclo lunar

        val percentIlluminated = ((dayInCycle + 1) / 29.53f * 100).toInt().toFloat()

        val phaseName = when (dayInCycle) {
            in 0..7 -> "Lua Nova"
            in 8..14 -> "Quarto Crescente"
            in 15..22 -> "Lua Cheia"
            else -> "Quarto Minguante"
        }

        // Qualidade de pesca baseada na fase
        val fishingQuality = when (dayInCycle) {
            in 0..2 -> "Ruim"
            in 3..7 -> "Regular"
            in 8..13 -> "Boa"
            in 14..17 -> "Excelente"
            in 18..22 -> "Boa"
            in 23..27 -> "Regular"
            else -> "Ruim"
        }

        val recommendation = when (phaseName) {
            "Lua Nova" -> "Evitar. Peixes ficam menos ativos."
            "Quarto Crescente" -> "Bom para pesca. Peixes mais ativos."
            "Lua Cheia" -> "Excelente! Peixes muito ativos, especialmente a noite."
            else -> "Regular. Atividade em declinio."
        }

        return MoonPhase(
            phaseName = phaseName,
            percentIlluminated = percentIlluminated,
            dayInCycle = dayInCycle,
            fishingQuality = fishingQuality,
            recommendation = recommendation
        )
    }

    /**
     * Analisa condicoes gerais para pesca
     */
    fun analyzeFishingConditions(
        latitude: Double,
        longitude: Double
    ): FishingWeatherData {
        val wind = getWindData(latitude, longitude)
        val tide = getTideData(latitude, longitude)
        val moon = getMoonPhase()

        // Calcular qualidade geral
        val windScore = when (wind.quality) {
            "Excelente" -> 4
            "Boa" -> 3
            "Regular" -> 2
            else -> 1
        }

        val moonScore = when (moon.fishingQuality) {
            "Excelente" -> 4
            "Boa" -> 3
            "Regular" -> 2
            else -> 1
        }

        val tideScore = when (tide.type) {
            "Subida" -> 4 // Enchente é ótima
            "Preamar" -> 3
            "Descida" -> 4 // Vazante também é ótima
            else -> 2 // Baixamar é ruim
        }

        val avgScore = (windScore + moonScore + tideScore) / 3
        val overallQuality = when {
            avgScore >= 3.5 -> "Excelente"
            avgScore >= 2.5 -> "Boa"
            avgScore >= 1.5 -> "Regular"
            else -> "Ruim"
        }

        val bestFishingTime = when {
            moon.phaseName.contains("Nova") -> "Evitar"
            tide.type == "Subida" -> "Madrugada a Manhã (Enchente)"
            tide.type == "Descida" -> "Tarde a Noite (Vazante)"
            else -> "Crepuscular (Amanhecer/Anoitecer)"
        }

        val warnings = mutableListOf<String>()
        if (wind.speedKmh > 25) warnings.add("Ventos fortes! Cuidado ao remar.")
        if (moon.phaseName == "Lua Nova") warnings.add("Lua nova: peixes menos ativos.")
        if (abs(tide.heightMeters - 2f) > 1f) warnings.add("Maré em picos: correntes fortes.")

        return FishingWeatherData(
            wind = wind,
            tide = tide,
            moonPhase = moon,
            overallQuality = overallQuality,
            bestFishingTime = bestFishingTime,
            warnings = warnings
        )
    }

    /**
     * Recomenda especies de peixes baseado nas condicoes
     */
    fun recommendFishSpecies(
        latitude: Double,
        longitude: Double
    ): List<String> {
        val conditions = analyzeFishingConditions(latitude, longitude)

        return when {
            conditions.overallQuality == "Excelente" -> listOf(
                "Dourado (Rio/Açude)",
                "Tucunaré (Água doce)",
                "Robalo (Costa)",
                "Linguado (Mar)"
            )
            conditions.overallQuality == "Boa" -> listOf(
                "Tilápia",
                "Badejo",
                "Corvina",
                "Mandi"
            )
            conditions.overallQuality == "Regular" -> listOf(
                "Traíra",
                "Mapara",
                "Piranhas (cuidado)"
            )
            else -> listOf(
                "Evitar pesca ou tentar água doce."
            )
        }
    }
}
