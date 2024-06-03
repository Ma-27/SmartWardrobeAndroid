package com.mamh.smartwardrobe.bean.netpacket


/**
@description
@author Mamh
@Date 2024/5/12
 **/

data class UsefulDailyWeatherDetail(
    var location: String = "重庆市 渝北区",         // 地理位置，格式为"Lat: 纬度, Lon: 经度"
    var temperature: Int = 23,         // 温度信息，平均温度
    var humidity: Int = 40,            // 湿度信息，平均湿度
    var pm25: Int = 78,                // PM2.5信息
    var dressingIndex: Int = 2,       // 穿衣指数，包含指数和描述,2代表比较炎热
    var dressingAdvice: String = "建议穿清爽衬衫，轻薄裤，舒适帆布鞋",   // 穿衣建议
    var weatherCondition: String = "晴朗",  // 天气状况，如“晴朗”或“阴天”
    var comfortIndex: Int = 6,       // 舒适度指数
    var comfortDescription: String = "未知的舒适度指数",  // 舒适度描述
)

