package ir.hamedmahmoodi.weathervision.ui.weather

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.hamedmahmoodi.weathervision.data.repository.WeatherRepository
import ir.hamedmahmoodi.weathervision.utils.AppLocaleUtil
import ir.hamedmahmoodi.weathervision.utils.CityLookupUtil
import ir.hamedmahmoodi.weathervision.utils.DEFAULT_WEATHER_DESTINATION
import ir.hamedmahmoodi.weathervision.utils.Result
import ir.hamedmahmoodi.weathervision.utils.TemperatureUnitUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WeatherUiState> =
        MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    private val _uiEvent = MutableSharedFlow<WeatherUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onMenuClicked() {
        viewModelScope.launch {
            _uiEvent.emit(WeatherUiEvent.OpenDrawer)
        }
    }

    fun onCloseDrawer() {
        viewModelScope.launch {
            _uiEvent.emit(WeatherUiEvent.CloseDrawer)
        }
    }

    private val _selectedLanguage = mutableStateOf(AppLocaleUtil.get())
    val selectedLanguage: State<LanguageOption> = _selectedLanguage

    fun updateLanguage(option: LanguageOption) {
        _selectedLanguage.value = option
        AppLocaleUtil.set(option)
    }

    private val _selectedTheme = mutableStateOf(ThemeOption.SYSTEM)
    val selectedTheme: State<ThemeOption> = _selectedTheme

    fun updateTheme(option: ThemeOption) {
        _selectedTheme.value = option
        when (option) {
            ThemeOption.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            ThemeOption.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeOption.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private val _selectedTemperatureUnit = mutableStateOf(TemperatureUnit.CELSIUS)
    val selectTemperatureUnit: State<TemperatureUnit> = _selectedTemperatureUnit

    fun updateTemperatureUnit(option: TemperatureUnit) {
        _selectedTemperatureUnit.value = option
        TemperatureUnitUtil.set(option)
    }

    init {
        getWeather()
    }

    fun getWeather(city: String = DEFAULT_WEATHER_DESTINATION) {
        val englishCity = CityLookupUtil.findEnglish(city)?:city
        repository.getWeatherForecast(englishCity).map { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.value = WeatherUiState(weather = result.data)
                }

                is Result.Error -> {
                    _uiState.value = WeatherUiState(errorMessage = result.errorMessage)
                }

                Result.Loading -> {
                    _uiState.value = WeatherUiState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}