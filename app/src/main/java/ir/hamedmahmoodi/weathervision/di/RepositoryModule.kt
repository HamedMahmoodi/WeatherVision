package ir.hamedmahmoodi.weathervision.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.hamedmahmoodi.weathervision.data.network.WeatherApi
import ir.hamedmahmoodi.weathervision.data.repository.DefaultWeatherRepository
import ir.hamedmahmoodi.weathervision.data.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(weatherApi: WeatherApi): WeatherRepository =
        DefaultWeatherRepository(weatherApi)
}