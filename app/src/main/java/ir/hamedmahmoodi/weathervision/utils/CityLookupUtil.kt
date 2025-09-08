package ir.hamedmahmoodi.weathervision.utils

import android.content.Context
import com.google.gson.Gson
import ir.hamedmahmoodi.weathervision.R
import ir.hamedmahmoodi.weathervision.model.City

object CityLookupUtil {

    private var cityList: List<City>? = null

    fun loadCities(context: Context) {
        if (cityList == null) {
            val json = context.resources.openRawResource(R.raw.iran_cities)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            cityList = Gson().fromJson(json, Array<City>::class.java).toList()
        }
    }

    fun findEnglish(cityFa: String): String? {
        return cityList?.firstOrNull { it.fa == cityFa }?.en
    }
}
