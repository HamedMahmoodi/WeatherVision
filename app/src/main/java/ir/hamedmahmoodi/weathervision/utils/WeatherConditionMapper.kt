package ir.hamedmahmoodi.weathervision.utils

import android.content.Context
import ir.hamedmahmoodi.weathervision.R

object WeatherConditionMapper {

    fun map(condition: String, context: Context): String {
        return when (condition.lowercase()) {
            "sunny" -> context.getString(R.string.condition_sunny)
            "clear" -> context.getString(R.string.condition_clear)
            "partly cloudy" -> context.getString(R.string.condition_partly_cloudy)
            "cloudy" -> context.getString(R.string.condition_cloudy)
            "overcast" -> context.getString(R.string.condition_overcast)
            "mist" -> context.getString(R.string.condition_mist)
            "patchy rain possible" -> context.getString(R.string.condition_patchy_rain)
            "rain" -> context.getString(R.string.condition_rain)
            "snow" -> context.getString(R.string.condition_snow)
            "thunderstorm" -> context.getString(R.string.condition_thunderstorm)
            "fog" -> context.getString(R.string.condition_fog)
            "drizzle" -> context.getString(R.string.condition_drizzle)
            else -> condition
        }
    }
}