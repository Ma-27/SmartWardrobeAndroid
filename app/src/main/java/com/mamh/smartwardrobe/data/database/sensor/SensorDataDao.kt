package com.mamh.smartwardrobe.data.database.sensor

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
interface SensorDataDao {
    @Query("SELECT * FROM sensor_data WHERE userId = :userId LIMIT 1")
    fun getSensorDataByUserId(userId: Int): LiveData<SensorDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorData(sensorData: SensorDataEntity)

    @Update
    suspend fun updateSensorData(sensorData: SensorDataEntity): Int

    @Query("DELETE FROM sensor_data")
    suspend fun deleteAllSensorData()

    @Transaction
    suspend fun insertSingleSensorData(sensorData: SensorDataEntity) {
        deleteAllSensorData()
        insertSensorData(sensorData)
    }
}
