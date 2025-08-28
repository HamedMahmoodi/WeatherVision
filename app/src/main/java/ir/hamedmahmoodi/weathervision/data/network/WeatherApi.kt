package ir.hamedmahmoodi.weathervision.data.network

import ir.hamedmahmoodi.weathervision.BuildConfig
import ir.hamedmahmoodi.weathervision.data.model.ForecastResponse
import ir.hamedmahmoodi.weathervision.utils.DEFAULT_WEATHER_DESTINATION
import ir.hamedmahmoodi.weathervision.utils.NUMBER_OF_DAYS
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WeatherApi {
    @FormUrlEncoded
    @POST("forecast.json")
    suspend fun getWeatherForecast(
        @Field("key") key: String = BuildConfig.API_KEY,
        @Field("q") city: String = DEFAULT_WEATHER_DESTINATION,
        @Field("days") days: Int = NUMBER_OF_DAYS,
    ): ForecastResponse
}