package com.mamh.smartwardrobe.bean.netpacket


/**
@description 天气API返回的数据类
@author Mamh
@Date 2024/5/11
 **/

data class DailyWeatherResponse(
    val status: String,
    val api_version: String,
    val api_status: String,
    val lang: String,
    val unit: String,
    val tzshift: Int,
    val timezone: String,
    val server_time: Long,
    val location: List<Double>,
    val result: DailyResult
)

data class DailyResult(
    val alert: Alert?,
    val daily: DailyData,
    val primary: Int
)

data class Alert(
    val status: String,
    val content: List<Any>,
    val adcodes: List<Adcode>
)

data class Adcode(
    val adcode: Int,
    val name: String
)

data class DailyData(
    val status: String,
    val astro: List<Astro>,
    val precipitation_08h_20h: List<DailyDetail>,
    val precipitation_20h_32h: List<DailyDetail>,
    val precipitation: List<DailyDetail>,
    val temperature: List<TemperatureDetail>,
    val temperature_08h_20h: List<TemperatureDetail>,
    val temperature_20h_32h: List<TemperatureDetail>,
    val wind: List<WindDetail>,
    val wind_08h_20h: List<WindDetail>,
    val wind_20h_32h: List<WindDetail>,
    val humidity: List<DailyDetail>,
    val cloudrate: List<DailyDetail>,
    val pressure: List<DailyDetail>,
    val visibility: List<DailyDetail>,
    val dswrf: List<DailyDetail>,
    val air_quality: AirQualityDaily,
    val skycon: List<SkyconDetail>,
    val skycon_08h_20h: List<SkyconDetail>,
    val skycon_20h_32h: List<SkyconDetail>,
    val life_index: LifeIndexDaily
)

data class Astro(
    val date: String,
    val sunrise: Sunrise,
    val sunset: Sunset
)

data class Sunrise(
    val time: String
)

data class Sunset(
    val time: String
)

data class DailyDetail(
    val date: String,
    val max: Double,
    val min: Double,
    val avg: Double,
    val probability: Int? // 将probability定义为可空类型
)

data class TemperatureDetail(
    val date: String,
    val max: Double, // 最高气温
    val min: Double, // 最低气温
    val avg: Double // 平均气温
)

data class WindDetail(
    val date: String,
    val max: Wind,
    val min: Wind,
    val avg: Wind
)

data class Wind(
    val speed: Double, // 风速
    val direction: Double // 风向
)

data class AirQualityDaily(
    val aqi: List<AQIDetail>,
    val pm25: List<DailyDetail>
)

data class AQIDetail(
    val date: String,
    val max: AQI,
    val avg: AQI,
    val min: AQI
)

data class AQI(
    val chn: Int, // 中国空气质量指数
    val usa: Int // 美国空气质量指数
)

data class SkyconDetail(
    val date: String,
    val value: String // 天气现象
)

data class LifeIndexDaily(
    val ultraviolet: List<IndexDetail>, // 紫外线指数
    val carWashing: List<IndexDetail>, // 洗车指数
    val dressing: List<IndexDetail>, // 穿衣指数
    val comfort: List<IndexDetail>, // 舒适度指数
    val coldRisk: List<IndexDetail> // 感冒风险指数
)

data class IndexDetail(
    val date: String,
    val index: String,
    val desc: String // 描述
)
