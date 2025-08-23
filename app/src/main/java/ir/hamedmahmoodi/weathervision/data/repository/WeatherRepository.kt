package ir.hamedmahmoodi.weathervision.data.repository

import ir.hamedmahmoodi.weathervision.model.Weather
import ir.hamedmahmoodi.weathervision.utils.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(city: String): Flow<Result<Weather>>
}