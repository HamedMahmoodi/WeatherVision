package ir.hamedmahmoodi.weathervision.ui.weather

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Devices.PIXEL_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ir.hamedmahmoodi.weathervision.R
import ir.hamedmahmoodi.weathervision.data.model.ForecastResponse.Current.Condition
import ir.hamedmahmoodi.weathervision.model.Forecast
import ir.hamedmahmoodi.weathervision.model.Hour
import ir.hamedmahmoodi.weathervision.model.Weather
import ir.hamedmahmoodi.weathervision.ui.theme.WeatherTheme
import ir.hamedmahmoodi.weathervision.ui.weather.components.Animation
import ir.hamedmahmoodi.weathervision.ui.weather.components.ForecastComponent
import ir.hamedmahmoodi.weathervision.ui.weather.components.HourlyComponent
import ir.hamedmahmoodi.weathervision.ui.weather.components.WeatherComponent
import ir.hamedmahmoodi.weathervision.utils.CityLookupUtil
import ir.hamedmahmoodi.weathervision.utils.DateUtil.formatDate
import ir.hamedmahmoodi.weathervision.utils.DateUtil.formatDay
import ir.hamedmahmoodi.weathervision.utils.DateUtil.formatFullDate
import ir.hamedmahmoodi.weathervision.utils.TemperatureUnitUtil
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState
    val uiState: WeatherUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectUnit by viewModel.selectTemperatureUnit
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        CityLookupUtil.loadCities(context)
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is WeatherUiEvent.OpenDrawer -> {
                    scope.launch { drawerState.open() }
                }

                is WeatherUiEvent.CloseDrawer -> {
                    scope.launch { drawerState.close() }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent =
        {
            WeatherDrawerContent(
                onCloseDrawer = { viewModel.onCloseDrawer() },
                isVisible = drawerState.isOpen,
                selectedLanguage = viewModel.selectedLanguage.value,
                onLanguageSelected = { viewModel.updateLanguage(it) },
                selectedTheme = viewModel.selectedTheme.value,
                onThemeSelected = { viewModel.updateTheme(it) },
                selectedTemperatureUnit = selectUnit,
                onTemperatureUnit = { viewModel.updateTemperatureUnit(it) },
                selectedDateType = viewModel.selectedDateType.value,
                onSelectedDateType = { viewModel.updateDateType(it) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                WeatherTopAppBar(
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchTextState,
                    onTextChange = { viewModel.updateSearchTextState(it) },
                    onCloseClicked = { viewModel.updateSearchWidgetState(SearchWidgetState.CLOSED) },
                    onSearchClicked = { viewModel.getWeather(it) },
                    onSearchTriggered = {
                        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                    },
                    onMenuClick = { scope.launch { viewModel.onMenuClicked() } },
                    viewModel = viewModel
                )
            },
            content = { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreenContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        selectedUnit = selectUnit
                    )
                }
            },
        )
    }

}

@Composable
fun WeatherScreenContent(
    uiState: WeatherUiState,
    viewModel: WeatherViewModel?,
    selectedUnit: TemperatureUnit,
) {
    when {
        uiState.isLoading -> {
            Animation(modifier = Modifier.fillMaxSize(), animation = R.raw.animation_loading)
            WeatherLoadingState()
        }

        uiState.errorMessage.isNotEmpty() -> {
            WeatherErrorState(uiState = uiState, viewModel = viewModel)
        }

        else -> {
            WeatherSuccessState(
                uiState = uiState,
                selectedTemperatureUnit = selectedUnit,
                selectedDateType = viewModel?.selectedDateType?.value ?: DateType.GREGORIAN
            )
        }
    }
}

@Composable
private fun WeatherLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Text(
            text = stringResource(R.string.text_loading),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun WeatherErrorState(
    uiState: WeatherUiState,
    viewModel: WeatherViewModel?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Animation(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            animation = R.raw.animation_error,
        )

        Button(onClick = { viewModel?.getWeather() }) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Retry",
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.retry),
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            modifier = Modifier
                .weight(2f)
                .alpha(0.5f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            text = stringResource(R.string.something_went_wrong, uiState.errorMessage),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WeatherSuccessState(
    uiState: WeatherUiState,
    selectedTemperatureUnit: TemperatureUnit,
    selectedDateType: DateType,
) {
    val backgroundImage = backgroundImageForCondition(
        condition = uiState.weather?.condition,
        isDay = uiState.weather?.isDay == 1
    )
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.8f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = uiState.weather?.name.orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = uiState.weather?.date
                    ?.formatFullDate(selectedDateType)
                    .orEmpty(),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    modifier = Modifier.size(120.dp),
                    model = stringResource(
                        R.string.icon_image_url,
                        uiState.weather?.condition?.icon.orEmpty(),
                    ),

                    contentDescription = null,
                    error = painterResource(R.drawable.ic_placeholder),
                    placeholder = painterResource(R.drawable.ic_placeholder),
                )
                Text(
                    text = uiState.weather?.temperature?.let { temp ->
                        TemperatureUnitUtil.formatTemperature(
                            temp.toDouble(),
                            selectedTemperatureUnit
                        )
                    }.orEmpty(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 50.sp
                )
                Text(
                    text = if (TemperatureUnit.CELSIUS.toString() == selectedTemperatureUnit.toString()) {
                        stringResource(R.string.unit_celsius)
                    } else {
                        stringResource(R.string.unit_fahrenheit)
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = uiState.weather?.condition?.text.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sunrise),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = uiState.weather?.forecasts?.firstOrNull()?.sunrise?.lowercase(Locale.US)
                        .orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_sunset),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = uiState.weather?.forecasts?.firstOrNull()?.sunset?.lowercase(Locale.US)
                        .orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    WeatherComponent(
                        modifier = Modifier.weight(1f),
                        weatherLabel = stringResource(R.string.feels_like),
                        weatherValue = uiState.weather?.feelsLike.toString(),
                        weatherUnit = when (selectedTemperatureUnit) {
                            TemperatureUnit.CELSIUS -> stringResource(R.string.unit_celsius)
                            TemperatureUnit.FAHRENHEIT -> stringResource(R.string.unit_fahrenheit)
                        },
                        iconId = R.drawable.ic_wind,
                    )
                    WeatherComponent(
                        modifier = Modifier.weight(1f),
                        weatherLabel = stringResource(R.string.visibility_label),
                        weatherValue = uiState.weather?.visibility.toString(),
                        weatherUnit = stringResource(R.string.visibility_unit),
                        iconId = R.drawable.ic_wind,
                    )
                    WeatherComponent(
                        modifier = Modifier.weight(1f),
                        weatherLabel = stringResource(R.string.pressure_label),
                        weatherValue = uiState.weather?.pressure.toString(),
                        weatherUnit = stringResource(R.string.pressure_unit),
                        iconId = R.drawable.ic_wind,
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    WeatherComponent(
                        modifier = Modifier.weight(1f),
                        weatherLabel = stringResource(R.string.wind_speed_label),
                        weatherValue = uiState.weather?.wind.toString(),
                        weatherUnit = stringResource(R.string.wind_speed_unit),
                        iconId = R.drawable.ic_wind,
                    )
                    WeatherComponent(
                        modifier = Modifier.weight(1f),
                        weatherLabel = stringResource(R.string.uv_index_label),
                        weatherValue = uiState.weather?.uv.toString(),
                        weatherUnit = "",
                        iconId = R.drawable.ic_uv,
                    )
                    WeatherComponent(
                        modifier = Modifier.weight(1f),
                        weatherLabel = stringResource(R.string.humidity_label),
                        weatherValue = uiState.weather?.humidity.toString(),
                        weatherUnit = stringResource(R.string.humidity_unit),
                        iconId = R.drawable.ic_humidity,
                    )
                }

            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 16.dp),
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, start = 16.dp),
            ) {
                uiState.weather?.forecasts?.get(0)?.let { forecast ->
                    items(forecast.hour) { hour ->
                        HourlyComponent(
                            time = hour.time,
                            icon = hour.icon,
                            temperature = TemperatureUnitUtil.formatTemperature(hour.temperature.toDouble()),
                            selectedUnit = selectedTemperatureUnit
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.forecast),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 16.dp),
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, start = 16.dp),
            ) {
                uiState.weather?.let { weather ->
                    items(weather.forecasts) { forecast ->
                        ForecastComponent(
                            day = forecast.date.formatDay(selectedDateType),
                            date = forecast.date.formatDate(selectedDateType),
                            icon = forecast.icon,
                            minTemp = TemperatureUnitUtil.formatTemperature(forecast.minTemp.toDouble()),
                            maxTemp = TemperatureUnitUtil.formatTemperature(forecast.maxTemp.toDouble()),
                            selectedUnit = selectedTemperatureUnit
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@DrawableRes
private fun backgroundImageForCondition(condition: Condition?, isDay: Boolean): Int {
    if (condition == null) {
        return if (isDay) R.drawable.bg_sunny else R.drawable.bg_clear
    }
    val code = condition.code
    return when (code) {

        //کمی ابری
        1003 -> if (isDay) R.drawable.bg_little_cloudy_day else R.drawable.bg_little_cloudy_night

        //ابری
        1006, 1009 -> if (isDay) R.drawable.bg_cloudy_day else R.drawable.bg_cloudy_night

        //مه - دود
        1030, 1135, 1147 -> if (isDay) R.drawable.bg_fog_day else R.drawable.bg_fog_night

        // باران
        1063, 1150, 1153, 1180, 1183, 1186, 1189 -> if (isDay) R.drawable.bg_rain_day else R.drawable.bg_rain_night

        // باران شدید
        1192, 1195, 1240, 1243, 1246 -> if (isDay) R.drawable.bg_rain_heavy_day else R.drawable.bg_rain_heavy_night

        // باران یخ زده
        1069, 1072, 1168, 1171, 1204, 1207, 1249, 1252 -> if (isDay) R.drawable.bg_sleet_day else R.drawable.bg_sleet_night

        // برف
        1066, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1237, 1255, 1258, 1261, 1264 -> if (isDay) R.drawable.bg_snow_day else R.drawable.bg_snow_night

        // رعد و برق - طوفان
        1087, 1273, 1276, 1279, 1282 -> if (isDay) R.drawable.bg_thunder_day else R.drawable.bg_thunder_night
        //صاف - تمیز
        else -> if (isDay) R.drawable.bg_sunny else R.drawable.bg_clear
    }
}

@SuppressLint("DefaultLocale")
@Preview(name = "Light Mode", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true,
    device = PIXEL_XL
)
@Composable
fun WeatherScreenContentPreview() {
    val hourlyForecast = mutableListOf<Hour>()
    for (i in 0 until 24) {
        hourlyForecast.add(
            Hour(
                "yyyy-mm-dd ${String.format("%02d", i)}",
                "",
                "${Random.nextInt(18, 21)}"
            )
        )
    }
    val forecasts = mutableListOf<Forecast>()
    for (i in 0..9) {
        forecasts.add(
            Forecast(
                "2023-10-${String.format("%02d", i)}",
                "${Random.nextInt(18, 21)}",
                "${Random.nextInt(10, 15)}",
                "07:20 am",
                "06:40 pm",
                "",
                hourlyForecast
            )
        )
    }
    WeatherTheme {
        Surface {
            WeatherScreenContent(
                WeatherUiState(
                    Weather(
                        temperature = 19,
                        date = "Oct 7",
                        wind = 22,
                        humidity = 35,
                        feelsLike = 18,
                        condition = Condition(10, "", "Cloudy"),
                        isDay = 1,
                        uv = 2,
                        name = "Munich",
                        visibility = 10,
                        pressure = 1005,
                        forecasts = forecasts,
                    ),
                ),
                viewModel = null,
                selectedUnit = TemperatureUnit.CELSIUS
            )
        }
    }
}

@Composable
fun WeatherTopAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    onMenuClick: () -> Unit,
    viewModel: WeatherViewModel,
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onSearchClicked = onSearchTriggered,
                onMenuClick = onMenuClick
            )
        }

        SearchWidgetState.OPENED -> {
            Column {
                SearchAppBar(
                    text = searchTextState,
                    onTextChange = {
                        onTextChange(it)
                        viewModel.searchCity(it)
                    },
                    onCloseClicked = onCloseClicked,
                    onSearchClicked = onSearchClicked
                )

                val results by viewModel.searchResults.collectAsStateWithLifecycle()
                if (results.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                    ) {
                        items(results) { city ->
                            Text(
                                text = "${city.name}, ${city.country}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onCitySelected(city)
                                    }
                                    .padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    onSearchClicked: () -> Unit,
    onMenuClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        actions = {
            IconButton(
                onClick = { onSearchClicked() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_icon),
                )
            }
        }
    )
}

@Composable
@Preview
fun DefaultAppBarPreview() {
    DefaultAppBar(onSearchClicked = {}, onMenuClick = {})
}

@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = { onTextChange(it) },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.5f),
                    text = stringResource(R.string.search_hint),
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier.alpha(0.7f),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_icon),
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_icon),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                    keyboardController?.hide()
                },
            ),
        )
    }
}

@Composable
@Preview
fun SearchAppBarPreview() {
    SearchAppBar(
        text = "Search for a city",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {}
    )
}

@Composable
fun WeatherDrawerContent(
    onCloseDrawer: () -> Unit,
    isVisible: Boolean,
    selectedLanguage: LanguageOption,
    onLanguageSelected: (LanguageOption) -> Unit,
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    selectedTemperatureUnit: TemperatureUnit,
    onTemperatureUnit: (TemperatureUnit) -> Unit,
    selectedDateType: DateType,
    onSelectedDateType: (DateType) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .padding(5.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { })
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_menu)
                )
            }

            this@Card.AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500)) + slideInHorizontally(initialOffsetX = { -it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    DrawerItems(
                        text = R.string.language_selection,
                        options = LanguageOption.entries,
                        selected = selectedLanguage,
                        onSelected = onLanguageSelected,
                        labelRes = { stringResource(it.labelRes) }
                    )
                    DrawerItems(
                        text = R.string.theme_selection,
                        options = ThemeOption.entries,
                        selected = selectedTheme,
                        onSelected = onThemeSelected,
                        labelRes = { stringResource(it.labelRes) }
                    )
                    DrawerItems(
                        text = R.string.temperature_selection,
                        options = TemperatureUnit.entries,
                        selected = selectedTemperatureUnit,
                        onSelected = onTemperatureUnit,
                        labelRes = { stringResource(it.labelRes) }
                    )
                    DrawerItems(
                        text = R.string.dateType_selection,
                        options = DateType.entries,
                        selected = selectedDateType,
                        onSelected = onSelectedDateType,
                        labelRes = { stringResource(it.labelRes) }
                    )

                }
            }

        }
    }
}

@Composable
fun <T> DrawerItems(
    @StringRes text: Int,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    labelRes: @Composable (T) -> String,
) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(5.dp))

    options.forEach {
        SelectableOptionRow(
            text = labelRes(it),
            selected = (selected == it),
            onClick = { onSelected(it) },
            leading = {
                RadioButton(
                    selected = (selected == it),
                    onClick = { onSelected(it) },
                    modifier = Modifier.size(35.dp)
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    HorizontalDivider(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        thickness = 1.dp
    )

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_7_PRO
)
fun WeatherDrawerContentPreview() {
    WeatherDrawerContent(
        onCloseDrawer = {},
        isVisible = true,
        selectedLanguage = LanguageOption.SYSTEM,
        onLanguageSelected = {},
        selectedTheme = ThemeOption.SYSTEM,
        onThemeSelected = {},
        selectedTemperatureUnit = TemperatureUnit.CELSIUS,
        onTemperatureUnit = {},
        selectedDateType = DateType.GREGORIAN,
        onSelectedDateType = {}
    )
}

@Composable
private fun SelectableOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: (@Composable () -> Unit)? = null,
) {
    val targetBg = if (selected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else
        Color.Transparent
    val bgColor by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "bgAnim"
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnim"
    )

    val tonalElevation by animateDpAsState(
        targetValue = if (selected) 5.dp else 0.dp,
        animationSpec = tween(180),
        label = "elevAnim"
    )

    Surface(
        tonalElevation = tonalElevation,
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            if (leading != null) {
                leading()
            }
            val textColor = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = textColor,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}



