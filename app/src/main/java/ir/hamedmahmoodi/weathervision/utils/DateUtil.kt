package ir.hamedmahmoodi.weathervision.utils

import ir.hamedmahmoodi.weathervision.ui.weather.DateType
import ir.huri.jcal.JalaliCalendar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private val persianMonths = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    fun String.formatDate(type: DateType): String {
        return try {
            val parts = this.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            when (type) {
                DateType.GREGORIAN -> {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
                    val date = inputFormat.parse(this)
                    if (date != null) outputFormat.format(date) else this
                }

                DateType.JALALI -> {
                    val jalali = JalaliCalendar(GregorianCalendar(year, month - 1, day))
                    val monthName = persianMonths[jalali.month - 1]
                    "${jalali.day} $monthName"
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            this
        }
    }

    fun String.formatDay(type: DateType): String {
        return try {
            val parts = this.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            when (type) {
                DateType.GREGORIAN -> {
                    val calendar = GregorianCalendar(year, month - 1, day)
                    val outputDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    outputDateFormat.format(calendar.time)
                }

                DateType.JALALI -> {
                    val jalali = JalaliCalendar(GregorianCalendar(year, month - 1, day))
                    jalali.dayOfWeekString
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            this
        }
    }

    fun String.formatFullDate(type: DateType): String {
        val day = this.formatDay(type)
        val date = this.formatDate(type)
        return "$day، $date"
    }

}
