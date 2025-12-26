package com.dutra.agente.fishing

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Integração com APIs de Clima para suporte ao Agente de Pesca
 * Suporta OpenWeatherMap e outros serviços meteorológicos
 */

data class WeatherData(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDegrees: Int,
    val cloudiness: Int,
    val visibility: Int,
    val precipitation: Double = 0.0,
    val description: String,
    val icon: String
)

data class WeatherForecast(
    val timestamp: Long,
    val weather: WeatherData,
    val rainProbability: Int
)

// Retrofit Interface para OpenWeatherMap API
interface WeatherAPIService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): Response<OpenWeatherResponse>

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "pt_br"
    ): Response<ForecastResponse>
}

data class OpenWeatherResponse(
    val coord: Coordinates,
    val weather: List<WeatherDescription>,
    val main: MainWeatherData,
    val visibility: Int,
    val wind: WindInfo,
    val clouds: Clouds,
    val rain: RainInfo? = null,
    val snow: SnowInfo? = null,
    val dt: Long,
    val sys: SystemInfo,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coordinates(
    val lon: Double,
    val lat: Double
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainWeatherData(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class WindInfo(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

data class Clouds(
    val all: Int
)

data class RainInfo(
    val `1h`: Double? = null,
    val `3h`: Double? = null
)

data class SnowInfo(
    val `1h`: Double? = null,
    val `3h`: Double? = null
)

data class SystemInfo(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt: Long,
    val main: MainWeatherData,
    val weather: List<WeatherDescription>,
    val clouds: Clouds,
    val wind: WindInfo,
    val visibility: Int,
    val pop: Double,
    val rain: RainInfo? = null,
    val snow: SnowInfo? = null,
    val sys: ForecastSysInfo
)

data class ForecastSysInfo(
    val pod: String
)

/**
 * Manager para integração com Weather APIs
 */
class WeatherAPIManager(
    private val apiService: WeatherAPIService,
    private val apiKey: String
) {
    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> = _currentWeather

    private val _forecast = MutableStateFlow<List<WeatherForecast>>(emptyList())
    val forecast: StateFlow<List<WeatherForecast>> = _forecast

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    suspend fun fetchCurrentWeather(latitude: Double, longitude: Double) {
        _isLoading.emit(true)
        try {
            val response = apiService.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            
            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    val weatherData = WeatherData(
                        temperature = weatherResponse.main.temp,
                        feelsLike = weatherResponse.main.feels_like,
                        humidity = weatherResponse.main.humidity,
                        pressure = weatherResponse.main.pressure,
                        windSpeed = weatherResponse.wind.speed,
                        windDegrees = weatherResponse.wind.deg,
                        cloudiness = weatherResponse.clouds.all,
                        visibility = weatherResponse.visibility,
                        description = weatherResponse.weather.firstOrNull()?.description ?: "",
                        icon = weatherResponse.weather.firstOrNull()?.icon ?: ""
                    )
                    _currentWeather.emit(weatherData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.emit(false)
        }
    }

    suspend fun fetchForecast(latitude: Double, longitude: Double) {
        _isLoading.emit(true)
        try {
            val response = apiService.getForecast(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            
            if (response.isSuccessful) {
                response.body()?.let { forecastResponse ->
                    val forecastList = forecastResponse.list.map { item ->
                        WeatherForecast(
                            timestamp = item.dt * 1000,
                            weather = WeatherData(
                                temperature = item.main.temp,
                                feelsLike = item.main.feels_like,
                                humidity = item.main.humidity,
                                pressure = item.main.pressure,
                                windSpeed = item.wind.speed,
                                windDegrees = item.wind.deg,
                                cloudiness = item.clouds.all,
                                visibility = item.visibility,
                                precipitation = item.rain?.`1h` ?: 0.0,
                                description = item.weather.firstOrNull()?.description ?: "",
                                icon = item.weather.firstOrNull()?.icon ?: ""
                            ),
                            rainProbability = (item.pop * 100).toInt()
                        )
                    }
                    _forecast.emit(forecastList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.emit(false)
        }
    }
}

// Extensão para determinar se o clima é favorável à pesca
fun WeatherData.isFavorableForFishing(): Boolean {
    val windFavorable = windSpeed in 5.0..20.0
    val visibilityGood = visibility > 5000
    val rainAcceptable = precipitation < 10.0
    val tempComfortable = temperature in 15.0..28.0
    
    return windFavorable && visibilityGood && rainAcceptable && tempComfortable
}
