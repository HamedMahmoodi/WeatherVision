package ir.hamedmahmoodi.weathervision.ui.weather

import ir.hamedmahmoodi.weathervision.model.Weather

data class WeatherUiState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)
