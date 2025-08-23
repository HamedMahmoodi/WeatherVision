package ir.hamedmahmoodi.weathervision.utils

sealed class Result<out T : Any> {
    data object Loading : Result<Nothing>()
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val errorMessage: String) : Result<Nothing>()
}
