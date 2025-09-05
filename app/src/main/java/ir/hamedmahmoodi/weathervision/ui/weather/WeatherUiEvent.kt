package ir.hamedmahmoodi.weathervision.ui.weather

sealed class WeatherUiEvent {
    data object OpenDrawer : WeatherUiEvent()
    data object CloseDrawer : WeatherUiEvent()
}