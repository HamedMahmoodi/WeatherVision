package ir.hamedmahmoodi.weathervision.utils

import ir.hamedmahmoodi.weathervision.ui.weather.TemperatureUnit

object TemperatureUnitUtil {

    private var currentUnit: TemperatureUnit = TemperatureUnit.CELSIUS

    fun set(option: TemperatureUnit) {
        currentUnit = option
    }

    fun get(): TemperatureUnit = currentUnit

    fun formatTemperature(tempCelsius: Double, unit: TemperatureUnit = currentUnit): String {
        return when (unit) {
            TemperatureUnit.CELSIUS -> "${tempCelsius.toInt()}°C"
            TemperatureUnit.FAHRENHEIT -> "${celsiusToFahrenheit(tempCelsius).toInt()}°F"
        }
    }


    private fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }
}
