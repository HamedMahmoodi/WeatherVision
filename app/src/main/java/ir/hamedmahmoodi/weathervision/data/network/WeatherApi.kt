package ir.hamedmahmoodi.weathervision.data.network

import ir.hamedmahmoodi.weathervision.BuildConfig
import ir.hamedmahmoodi.weathervision.data.model.ForecastResponse
import ir.hamedmahmoodi.weathervision.utils.DEFAULT_WEATHER_DESTINATION
import ir.hamedmahmoodi.weathervision.utils.NUMBER_OF_DAYS
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("q") city: String = DEFAULT_WEATHER_DESTINATION,
        @Query("days") days: Int = NUMBER_OF_DAYS,
    ): ForecastResponse
}