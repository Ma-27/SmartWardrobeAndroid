package com.mamh.smartwardrobe.util

import android.net.wifi.ScanResult
import com.mamh.smartwardrobe.bean.flag.NetWorkDefaultConfiguration
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.bean.item.ClothItemForMCU
import com.mamh.smartwardrobe.bean.item.WifiItem
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.data.database.cloth.ClothItemEntity
import com.mamh.smartwardrobe.data.database.weather.WeatherEntity
import java.text.SimpleDateFormat
import java.util.Locale

//data class转换类，负责将一个data class转换为另一个，或者提取数据并映射
class DataTransferObject {
    companion object {
        private val inputDateFormat = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
        private val outputDateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())

        private fun convertDate(date: String): String {
            return try {
                val parsedDate = inputDateFormat.parse(date)
                parsedDate?.let {
                    outputDateFormat.format(it)
                } ?: "Unknown"
            } catch (e: Exception) {
                "Unknown"
            }
        }


        //使用map映射将每个对象转化为另一个对象
        //FIXME ，这里的设备mac地址写死了，实际情况下待wifi进行分配
        fun asWifiItem(scanResult: List<ScanResult>): List<WifiItem> {
            return scanResult.map {
                WifiItem(
                    wifiName = it.SSID,
                    apMacAddress = it.BSSID,
                    signalStrength = it.capabilities,
                    frequency = it.frequency,
                    isConnected = "未连接",
                    password = "",
                    deviceMacAddress = NetWorkDefaultConfiguration.DEFAULT_MAC_ADDRESS,
                    deviceIpAddress = NetWorkDefaultConfiguration.DEFAULT_IP_ADDRESS,
                    apIpAddress = "",
                    portNumber = NetWorkDefaultConfiguration.DEFAULT_PORT_NUMBER
                )
            }
        }


        // 将WeatherEntity转换为UsefulDailyWeatherDetail
        fun toUsefulDailyWeatherDetail(weather: WeatherEntity): UsefulDailyWeatherDetail {
            return UsefulDailyWeatherDetail(
                location = weather.location,
                temperature = weather.temperature,
                humidity = weather.humidity,
                pm25 = weather.pm25,
                dressingIndex = weather.dressingIndex,
                dressingAdvice = weather.dressingAdvice,
                weatherCondition = weather.weatherCondition,
                comfortIndex = weather.comfortIndex,
                comfortDescription = weather.comfortDescription
            )
        }


        // 将UsefulDailyWeatherDetail转换为WeatherEntity
        fun toWeatherEntity(weatherDetail: UsefulDailyWeatherDetail): WeatherEntity {
            return WeatherEntity(
                location = weatherDetail.location,
                temperature = weatherDetail.temperature,
                humidity = weatherDetail.humidity,
                pm25 = weatherDetail.pm25,
                dressingIndex = weatherDetail.dressingIndex,
                dressingAdvice = weatherDetail.dressingAdvice,
                weatherCondition = weatherDetail.weatherCondition,
                comfortIndex = weatherDetail.comfortIndex,
                comfortDescription = weatherDetail.comfortDescription
            )
        }


        // 生成默认的UsefulDailyWeatherDetail
        fun generateDefaultWeatherDetail(): UsefulDailyWeatherDetail {
            return UsefulDailyWeatherDetail()
        }

        // 静态函数：将 ClothItem 转换为 ClothItemForMCU
        fun fromClothItemToMCU(clothItem: ClothItem): ClothItemForMCU {
            val primaryColor = clothItem.colors.firstOrNull() ?: "#FFFFFF" // 选择第一个颜色或白色
            val convertedPurchaseDate = convertDate(clothItem.purchaseDate)
            val convertedLastWornDate = convertDate(clothItem.lastWornDate)

            return ClothItemForMCU(
                id = clothItem.id,
                color = primaryColor,
                style = clothItem.style,
                material = clothItem.material,
                size = clothItem.size,
                isInCloset = clothItem.isInCloset,
                hangPosition = clothItem.hangPosition,
                brand = clothItem.brand,
                purchaseDate = convertedPurchaseDate,
                isClean = clothItem.isClean,
                lastWornDate = convertedLastWornDate
            )
        }


        // 静态函数：将 ClothItem 转换为 ClothItemEntity
        fun fromClothItemToEntity(clothItem: ClothItem, userId: Int): ClothItemEntity {
            return ClothItemEntity(
                clothId = clothItem.id,
                userId = userId,
                name = clothItem.name,
                category = clothItem.category,
                colors = clothItem.colors,
                style = clothItem.style,
                material = clothItem.material,
                size = clothItem.size,
                isInCloset = clothItem.isInCloset,
                hangPosition = clothItem.hangPosition,
                brand = clothItem.brand,
                purchaseDate = clothItem.purchaseDate,
                isClean = clothItem.isClean,
                lastWornDate = clothItem.lastWornDate,
                tags = clothItem.tags,
                recommendationScore = clothItem.recommendationScore
            )
        }


        // 静态函数：将 ClothItemEntity 转换为 ClothItem
        fun fromClothItemEntityToClothItem(clothEntity: ClothItemEntity): ClothItem {
            return ClothItem(
                id = clothEntity.clothId,
                category = clothEntity.category,
                name = clothEntity.name,
                colors = clothEntity.colors.toMutableList(),
                style = clothEntity.style,
                material = clothEntity.material,
                size = clothEntity.size,
                isInCloset = clothEntity.isInCloset,
                hangPosition = clothEntity.hangPosition,
                brand = clothEntity.brand,
                purchaseDate = clothEntity.purchaseDate,
                isClean = clothEntity.isClean,
                lastWornDate = clothEntity.lastWornDate,
                tags = clothEntity.tags.toMutableList(),
                recommendationScore = clothEntity.recommendationScore
            )
        }

        // 静态函数：将 ClothItemEntity 列表转换为 ClothItem 列表
        fun fromClothItemEntityListToClothItemList(clothEntities: List<ClothItemEntity>): List<ClothItem> {
            return clothEntities.map { fromClothItemEntityToClothItem(it) }
        }

    }
}