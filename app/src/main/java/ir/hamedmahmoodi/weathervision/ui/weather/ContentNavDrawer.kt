package ir.hamedmahmoodi.weathervision.ui.weather

import androidx.annotation.StringRes
import ir.hamedmahmoodi.weathervision.R

enum class LanguageOption(@StringRes val labelRes: Int) {
    SYSTEM(R.string.lang_system),
    PERSIAN(R.string.lang_persian),
    ENGLISH(R.string.lang_english)
}

enum class ThemeOption(@StringRes val labelRes: Int){
    SYSTEM(R.string.theme_system),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark)
}

enum class TemperatureUnit(@StringRes val labelRes: Int){
    CELSIUS(R.string.unit_celsius),
    FAHRENHEIT(R.string.unit_fahrenheit)
}

enum class DateType(@StringRes val labelRes: Int) {
    GREGORIAN(R.string.type_gregorian),
    JALALI(R.string.type_jalali)
}