package com.mamh.smartwardrobe.bean.netpacket


/**
@description
@author Mamh
@Date 2024/5/12
 **/


data class RealtimeWeatherResponse(
    val temp: String
    /*
    val status: String,
    @Json(name = "temperature") val temperature: Double, // 地表 2 米气温
    @Json(name = "apparent_temperature") val apparent_temperature: Double, // 体感温度
    @Json(name = "pressure") val pressure: Double, // 地面气压
    @Json(name = "humidity") val humidity: Double, // 地表 2 米湿度相对湿度(%)
    @Json(name = "cloudrate") val cloudrate: Double, // 总云量(0.0-1.0)
    @Json(name = "skycon") val skycon: String, // 天气现象
    @Json(name = "visibility") val visibility: Double, // 地表水平能见度
    @Json(name = "dswrf") val dswrf: Double, // 向下短波辐射通量(W/M2)
    @Json(name = "wind") val wind: Wind, // 风相关数据
    @Json(name = "precipitation") val precipitation: WeatherPrecipitation, // 降水相关数据
    @Json(name = "air_quality") val air_quality: WeatherAirQuality, // 空气质量相关数据
    @Json(name = "life_index") val life_index: LifeIndex // 生活指数相关数据

     */
)

/*
data class WeatherWind(
    @Json(name = "direction") val direction: Double, // 地表 10 米风向
    @Json(name = "speed") val speed: Double // 地表 10 米风速
)

data class WeatherPrecipitation(
    @Json(name = "local") val local: LocalPrecipitation, // 本地降水数据
    @Json(name = "nearest") val nearest: NearestPrecipitation // 最近降水数据
)

data class LocalPrecipitation(
    val status: String,
    val datasource: String,
    @Json(name = "intensity") val intensity: Double // 本地降水强度
)

data class NearestPrecipitation(
    val status: String,
    @Json(name = "distance") val distance: Double, // 最近降水带与本地的距离
    @Json(name = "intensity") val intensity: Double // 最近降水处的降水强度
)

data class WeatherAirQuality(
    @Json(name = "pm25") val pm25: Int, // PM25 浓度(μg/m3)
    @Json(name = "pm10") val pm10: Int, // PM10 浓度(μg/m3)
    @Json(name = "o3") val o3: Int, // 臭氧浓度(μg/m3)
    @Json(name = "so2") val so2: Int, // 二氧化硫浓度(μg/m3)
    @Json(name = "no2") val no2: Int, // 二氧化氮浓度(μg/m3)
    @Json(name = "co") val co: Double, // 一氧化碳浓度(mg/m3)
    @Json(name = "aqi") val realtimeAqi: RealtimeAQI, // AQI 数据
    @Json(name = "description") val description: AQIDescription // 空气质量描述
)

data class RealtimeAQI(
    @Json(name = "chn") val chn: Int, // 国标 AQI
    @Json(name = "usa") val usa: Int
)

data class AQIDescription(
    @Json(name = "chn") val chn: String, // 中文空气质量描述
    @Json(name = "usa") val usa: String // 英文空气质量描述
)

*/