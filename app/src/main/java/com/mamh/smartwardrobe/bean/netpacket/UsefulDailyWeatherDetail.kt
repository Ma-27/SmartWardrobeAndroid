package com.mamh.smartwardrobe.bean.netpacket


/**
@description
@author Mamh
@Date 2024/5/12
 **/

data class UsefulDailyWeatherDetail(
    val location: String,         // 地理位置，格式为"Lat: 纬度, Lon: 经度"
    val temperature: Int,         // 温度信息，平均温度
    val humidity: Int,            // 湿度信息，平均湿度
    val pm25: Int,                // PM2.5信息，包含最高、最低和平均值
    val dressingIndex: Int,       // 穿衣指数，包含指数和描述
    val dressingAdvice: String,   // 穿衣建议
    val weatherCondition: String  // 天气状况，如“晴朗”或“阴天”
)

