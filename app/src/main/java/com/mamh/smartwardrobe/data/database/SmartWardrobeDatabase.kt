package com.mamh.smartwardrobe.data.database


/**
@description
@author Mamh
@Date 2024/5/30
 **/

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mamh.smartwardrobe.data.database.cloth.ClothDao
import com.mamh.smartwardrobe.data.database.cloth.ClothItemEntity
import com.mamh.smartwardrobe.data.database.sensor.SensorDataDao
import com.mamh.smartwardrobe.data.database.sensor.SensorDataEntity
import com.mamh.smartwardrobe.data.database.user.UserDao
import com.mamh.smartwardrobe.data.database.user.UserEntity
import com.mamh.smartwardrobe.data.database.weather.WeatherDao
import com.mamh.smartwardrobe.data.database.weather.WeatherEntity

@Database(
    entities = [UserEntity::class, SensorDataEntity::class, ClothItemEntity::class, WeatherEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(DataTypeConverters::class)
abstract class SmartWardrobeDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val sensorDataDao: SensorDataDao
    abstract val clothDao: ClothDao
    abstract val weatherDao: WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: SmartWardrobeDatabase? = null

        fun getInstance(context: Context): SmartWardrobeDatabase {
            // 如果INSTANCE已经初始化，直接返回
            return INSTANCE ?: synchronized(this) {
                // 再次检查INSTANCE是否已被初始化
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SmartWardrobeDatabase::class.java,
                    "smart_wardrobe_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
                instance
            }
        }
    }
}

