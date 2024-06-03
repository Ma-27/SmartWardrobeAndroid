package com.mamh.smartwardrobe.data.database.weather


/**
@description
@author Mamh
@Date 2024/5/30
 **/

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val weatherId: Int = 0,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "temperature")
    val temperature: Int,

    @ColumnInfo(name = "humidity")
    val humidity: Int,

    @ColumnInfo(name = "pm25")
    val pm25: Int,

    @ColumnInfo(name = "dressingIndex")
    val dressingIndex: Int,

    @ColumnInfo(name = "dressingAdvice")
    val dressingAdvice: String,

    @ColumnInfo(name = "weatherCondition")
    val weatherCondition: String,

    @ColumnInfo(name = "comfortIndex")
    val comfortIndex: Int,

    @ColumnInfo(name = "comfortDescription")
    val comfortDescription: String
)
