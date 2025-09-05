package ir.hamedmahmoodi.weathervision.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ir.hamedmahmoodi.weathervision.ui.weather.LanguageOption

object AppLocaleUtil {

    fun set(option: LanguageOption) {
        val locales = when (option) {
            LanguageOption.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
            LanguageOption.PERSIAN -> LocaleListCompat.forLanguageTags("fa")
            LanguageOption.ENGLISH -> LocaleListCompat.forLanguageTags("en")
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun get(): LanguageOption {
        val current = AppCompatDelegate.getApplicationLocales()
        if (current.isEmpty) return LanguageOption.SYSTEM

        return when (current[0]?.language) {
            "fa" -> LanguageOption.PERSIAN
            "en" -> LanguageOption.ENGLISH
            else -> LanguageOption.SYSTEM
        }
    }
}
