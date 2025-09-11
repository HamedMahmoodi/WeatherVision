package ir.hamedmahmoodi.weathervision.data.repository

import com.google.gson.JsonSyntaxException
import ir.hamedmahmoodi.weathervision.R
import ir.hamedmahmoodi.weathervision.data.model.SearchCityResponse
import ir.hamedmahmoodi.weathervision.data.model.toWeather
import ir.hamedmahmoodi.weathervision.data.network.WeatherApi
import ir.hamedmahmoodi.weathervision.model.Weather
import ir.hamedmahmoodi.weathervision.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WeatherRepository {
    override fun getWeatherForecast(city: String): Flow<Result<Weather>> = flow {
        emit(Result.Loading)
        try {
            val result = weatherApi.getWeatherForecast(city = city).toWeather()
            emit(Result.Success(result))
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    emit(Result.Error(exception.message.orEmpty()))
                }

                is IOException -> {
                    emit(Result.Error("Please check your network connection and try again!"))
                }

                is JsonSyntaxException -> {
                    emit(Result.Error("Something went wrong"))
                }

                else -> {
                    emit(Result.Error("Unknown error"))
                }
            }
        }
    }.flowOn(dispatcher)

    override suspend fun searchCities(query: String): Result<List<SearchCityResponse>> {
        return try {
            val cities = weatherApi.searchCities(query = query)
            Result.Success(cities)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

}
