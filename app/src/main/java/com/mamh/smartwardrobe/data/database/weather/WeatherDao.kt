package com.mamh.smartwardrobe.data.database.weather

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update


/**
@description
@author Mamh
@Date 2024/5/30
 **/

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather ORDER BY weatherId DESC LIMIT 1")
    fun getLatestWeatherData(): LiveData<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Update
    suspend fun updateWeather(weather: WeatherEntity): Int

    @Query("DELETE FROM weather")
    suspend fun deleteAllWeatherData()

    @Transaction
    suspend fun insertSingleWeather(weather: WeatherEntity) {
        deleteAllWeatherData()
        insertWeather(weather)
    }
}