package com.mamh.smartwardrobe.data.database.sensor


/**
@description
@author Mamh
@Date 2024/5/30
 **/

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sensor_data",
    /*
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
     */
    indices = [Index(value = ["userId"])]
)
data class SensorDataEntity(
    @PrimaryKey(autoGenerate = false)
    val sensorDataId: Int = 0, // 手动分配ID，确保只有一条记录

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "temperature")
    val temperature: Float,

    @ColumnInfo(name = "humidity")
    val humidity: Int,

    @ColumnInfo(name = "brightness")
    val brightness: Int,

    @ColumnInfo(name = "lightOpenness")
    val lightOpenness: Int,

    @ColumnInfo(name = "measureTime")
    val measureTime: Long,

    @ColumnInfo(name = "knobValue")
    val knobValue: Int,

    @ColumnInfo(name = "lightOn")
    val lightOn: Boolean,

    @ColumnInfo(name = "lightControlAuto")
    val lightControlAuto: Boolean
)
