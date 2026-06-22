package com.twr.developer.weatherguide.di

import com.twr.developer.weatherguide.data.local.dao.WeatherDao
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): com.twr.developer.weatherguide.data.local.database.WeatherDatabase {
        return Room.databaseBuilder(
            context,
            com.twr.developer.weatherguide.data.local.database.WeatherDatabase::class.java,
            "weather_db"
        ).build()
    }

    @Provides
    fun provideWeatherDao(db: com.twr.developer.weatherguide.data.local.database.WeatherDatabase): WeatherDao = db.weatherDao()
}