package com.mamh.smartwardrobe.ui.cloth

import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.data.AppRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @description 衣物推荐模型类，根据天气、温湿度、历史穿着记录等因素计算推荐指数，推荐合适的衣物
 */
object ClothModel {

    // 定义权重
    private var dressingIndexWeight = 0.4
    private var temperatureWeight = 0.2
    private var humidityWeight = 0.2
    private var wearHistoryWeight = 0.2

    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository

    // 定义穿衣指数对应的权重
    private val dressingIndexScores = mapOf(
        0 to 0.1,
        1 to 0.1,
        2 to 0.2,
        3 to 0.3,
        4 to 0.5,
        5 to 0.7,
        6 to 0.8,
        7 to 0.9,
        8 to 1.0
    )

    // 推荐函数，根据天气信息和衣物列表，计算推荐指数并推荐衣物
    fun recommend(
        weatherDetail: UsefulDailyWeatherDetail,
        clothes: List<ClothItem>
    ): List<ClothItem> {
        return clothes.filter { it.isInCloset && it.isClean }
            .map { cloth -> calculateRecommendationScore(weatherDetail, cloth) }
            .sortedByDescending { it.recommendationScore }
            .take(5)
    }

    // 推荐函数，根据天气信息和单个衣物，计算推荐指数并返回衣物评分
    fun recommend(
        weatherDetail: UsefulDailyWeatherDetail,
        cloth: ClothItem
    ): Double {
        return calculateRecommendationScore(weatherDetail, cloth).recommendationScore
    }

    // 推荐函数，根据单个衣物的情况，去Repository中获取天气信息，计算推荐指数并返回衣物评分
    fun recommend(
        cloth: ClothItem
    ): Double {
        val weatherDetail = repository.usefulDailyWeatherDetail.value
        val recommendationScore = weatherDetail?.let {
            calculateRecommendationScore(it, cloth).recommendationScore
        } ?: run {
            // 处理 weatherDetail 为空的情况，返回默认推荐指数
            0.0  // 或者其他默认值
        }
        return recommendationScore
    }

    // 计算推荐指数
    private fun calculateRecommendationScore(
        weatherDetail: UsefulDailyWeatherDetail,
        cloth: ClothItem
    ): ClothItem {
        val dressingScore = dressingIndexScores[weatherDetail.dressingIndex] ?: 0.0
        val temperatureScore = calculateTemperatureScore(weatherDetail.temperature, cloth)
        val humidityScore = calculateHumidityScore(weatherDetail.humidity, cloth)
        val wearHistoryScore = calculateWearHistoryScore(cloth.lastWornDate)

        val totalScore = sigmoid(dressingScore * dressingIndexWeight) +
                sigmoid(temperatureScore * temperatureWeight) +
                sigmoid(humidityScore * humidityWeight) +
                sigmoid(wearHistoryScore * wearHistoryWeight)

        cloth.recommendationScore = totalScore
        return cloth
    }

    // 计算温度适应性得分，根据衣物材质和设计
    private fun calculateTemperatureScore(temperature: Int, cloth: ClothItem): Double {
        val optimalTemperatureRange = when (cloth.material) {
            "wool" -> 0..10
            "cotton" -> 10..25
            "linen" -> 20..35
            else -> 15..25
        }
        return if (temperature in optimalTemperatureRange) 1.0 else 0.5
    }

    // 计算湿度适应性得分，根据衣物材质和设计
    private fun calculateHumidityScore(humidity: Int, cloth: ClothItem): Double {
        val optimalHumidityRange = when (cloth.material) {
            "wool" -> 30..50
            "cotton" -> 40..60
            "linen" -> 20..40
            else -> 35..55
        }
        return if (humidity in optimalHumidityRange) 1.0 else 0.5
    }

    // 计算历史穿着偏好得分，考虑最后一次穿着时间
    private fun calculateWearHistoryScore(lastWornDate: String): Double {
        val lastWornDaysAgo =
            (System.currentTimeMillis() - lastWornDate.toDate().time) / (1000 * 60 * 60 * 24)
        return if (lastWornDaysAgo > 30) 1.0 else 0.5
    }

    // 字符串转日期
    private fun String.toDate(): Date {
        val format = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
        return format.parse(this) ?: Date()
    }

    // Sigmoid函数，用于非线性处理
    private fun sigmoid(x: Double): Double {
        return 1 / (1 + Math.exp(-x))
    }

    private fun scaleToFive(score: Double): Double {
        return score * 5
    }
}
