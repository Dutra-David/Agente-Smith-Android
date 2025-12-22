package com.dutra.agente.essencial.localizacao

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val altitude: Double
)

data class LocationMetadata(
    val cidade: String = "",
    val estado: String = "",
    val tipoCorpoAgua: String = "", // Rio, Lago, Oceano, etc
    val distanciaTerrestre: Float = 0f // em metros
)

/**
 * Gerenciador de GPS para o Agente de Pesca
 * Fornece localizacao em tempo real e calculo de distancia
 */
class GPSLocationManager(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var currentLocation: LocationData? = null
    private var locationCallback: ((LocationData) -> Unit)? = null

    /**
     * Verifica permissoes de localizacao
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Inicia atualizacao de localizacao em tempo real
     * @param minTimeMs Tempo minimo entre atualizacoes (default: 5000ms)
     * @param minDistanceM Distancia minima para atualizar (default: 10m)
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        minTimeMs: Long = 5000,
        minDistanceM: Float = 10f,
        callback: (LocationData) -> Unit
    ) {
        if (!hasLocationPermission()) {
            throw SecurityException("Permissao ACCESS_FINE_LOCATION nao concedida")
        }

        locationCallback = callback

        try {
            // Tenta usar GPS primeiro (mais preciso)
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTimeMs,
                    minDistanceM,
                    { location ->
                        currentLocation = location.toLocationData()
                        currentLocation?.let { callback(it) }
                    }
                )
            }
            // Fallback para Network Provider
            else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTimeMs,
                    minDistanceM,
                    { location ->
                        currentLocation = location.toLocationData()
                        currentLocation?.let { callback(it) }
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Para atualizacoes de localizacao
     */
    fun stopLocationUpdates() {
        locationManager.removeUpdates { }
    }

    /**
     * Obtem ultima localizacao conhecida
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation(): LocationData? {
        return try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.toLocationData()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calcula distancia entre duas localizacoes em metros
     * Usa formula de Haversine para calculos de distancia em esfera
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val earthRadiusM = 6371000 // Raio da Terra em metros

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (earthRadiusM * c).toFloat()
    }

    /**
     * Calcula direcao (bearing) entre dois pontos em graus (0-360)
     * 0 = Norte, 90 = Leste, 180 = Sul, 270 = Oeste
     */
    fun calculateBearing(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) -
                sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x))

        return ((bearing + 360) % 360).toFloat()
    }

    /**
     * Obtem localizacao atual
     */
    fun getCurrentLocation(): LocationData? = currentLocation

    /**
     * Converte Location do Android para nosso data class
     */
    private fun Location.toLocationData() = LocationData(
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        timestamp = time,
        altitude = altitude
    )
}
