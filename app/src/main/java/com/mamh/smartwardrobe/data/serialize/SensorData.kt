package com.mamh.smartwardrobe.data.serialize


/**
@description
@author Mamh
@Date 2024/4/21
 **/

data class SensorData(
    val temperature: Float,
    val brightness: Int,
    val lightOpenness: Int,
    val humidity: Int,
    val measureTime: Long,
    val knobValue: Int
)

