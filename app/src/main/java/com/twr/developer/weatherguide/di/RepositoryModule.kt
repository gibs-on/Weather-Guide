package com.twr.developer.weatherguide.di

import android.content.Context
import com.twr.developer.weatherguide.data.remote.api.WeatherApiService
import com.twr.developer.weatherguide.data.local.dao.WeatherDao
import com.twr.developer.weatherguide.data.repository.WeatherRepositoryImpl
import com.twr.developer.weatherguide.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApiService,
        dao: WeatherDao,
        @ApplicationContext context: Context
    ): WeatherRepository {
        return WeatherRepositoryImpl(api, dao, context)
    }
}