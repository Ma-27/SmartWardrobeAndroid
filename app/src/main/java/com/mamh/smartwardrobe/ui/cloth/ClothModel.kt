package com.mamh.smartwardrobe.ui.cloth

import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.data.AppRepository
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.exp
import kotlin.math.pow

/**
 * @description 衣物推荐模型类，根据天气、温湿度、历史穿着记录等因素计算推荐指数，推荐合适的衣物
 */
object ClothModel {

    // 定义权重
    private var dressingIndexWeight = 0.45
    private var temperatureWeight = 0.15
    private var humidityWeight = 0.17
    private var wearHistoryWeight = 0.23

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
        return clothes.filter { it.isClean }
            .map { cloth -> calculateRecommendationScore(weatherDetail, cloth) }
            .sortedByDescending { it.recommendationScore }
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
        val dressingScore = calculateDressingScore(weatherDetail.dressingIndex, cloth)
        val temperatureScore = calculateTemperatureScore(weatherDetail.temperature, cloth)
        val humidityScore = calculateHumidityScore(weatherDetail.humidity, cloth)
        val wearHistoryScore = calculateWearHistoryScore(cloth.lastWornDate)

        // 计算原始总分，是通过sigmoid非线性后加权平均的分数
        val originalTotalScore = (
                customSigmoid(dressingScore) * dressingIndexWeight +
                        customSigmoid(temperatureScore) * temperatureWeight +
                        customSigmoid(humidityScore) * humidityWeight +
                        customSigmoid(wearHistoryScore) * wearHistoryWeight
                )

        Timber.d("originalTotalScore: $originalTotalScore")

        // 将0-1的评分值归一化到0-5
        var finalScore = scaleToFive(originalTotalScore)
        Timber.d("totalScore: $finalScore")

        // 二次重映射并处理异常
        // finalScore = remapScore(finalScore)
        // Timber.d("finalScore: $finalScore")

        Timber.d("==========================================================================================")

        cloth.recommendationScore = finalScore
        return cloth
    }

    // 计算穿衣指数得分，根据天气情况
    private fun calculateDressingScore(dressingIndex: Int, cloth: ClothItem): Double {
        val targetString = getDressingAdvice(dressingIndex)
        val patternString =
            cloth.material + cloth.style + cloth.category + cloth.name + cloth.tags.joinToString()

        // 计算patternString中包含的targetString的字数
        val matchingCharacters = targetString.toSet().count { it in patternString }

        // 定义一个映射函数，确保凹函数特性
        val baseScore = 0.32
        val maxMatchingCharacters = 15

        // 将匹配字符数限制在0到maxMatchingCharacters范围内
        val limitedMatchingCharacters = matchingCharacters.coerceIn(0, maxMatchingCharacters)
        Timber.d("matchingCharacters: $limitedMatchingCharacters, maxMatchingCharacters: $maxMatchingCharacters")

        // 使用凹函数（如二次函数）来映射分数
        val input = limitedMatchingCharacters.toDouble() / maxMatchingCharacters.toDouble()
        val normalizedScore = input * input

        // 计算最终分数
        val finalScore = baseScore + (1 - baseScore) * normalizedScore

        Timber.d("final dressing Score: $finalScore")

        return finalScore
    }

    // 计算温度适应性得分，根据衣物材质和设计
    private fun calculateTemperatureScore(temperature: Int, cloth: ClothItem): Double {
        val optimalTemperatureRange = when {
            cloth.material.contains("羊毛") -> 0..10
            cloth.material.contains("棉") -> 18..23
            cloth.material.contains("亚麻") -> 27..32
            cloth.material.contains("丝绸") -> 20..25
            cloth.material.contains("尼龙") -> 13..18
            cloth.material.contains("聚酯纤维") -> 17..22
            cloth.material.contains("皮革") -> 8..13
            cloth.material.contains("羊绒") -> 3..8
            cloth.material.contains("涤纶") -> 17..22
            cloth.material.contains("氨纶") -> 17..22
            cloth.material.contains("竹纤维") -> 22..27
            cloth.material.contains("天丝") -> 22..27
            else -> {
                Timber.d("material not found: ${cloth.material},return 0.5")
                return 0.5
            } // 匹配不到，则返回中间值，此算法无效。
        }

        val temperatureScore = when {
            temperature in optimalTemperatureRange -> 1.0
            temperature in (optimalTemperatureRange.first - 1)..(optimalTemperatureRange.last + 1) -> 0.9
            temperature in (optimalTemperatureRange.first - 2)..(optimalTemperatureRange.last + 2) -> 0.8
            temperature in (optimalTemperatureRange.first - 3)..(optimalTemperatureRange.last + 3) -> 0.7
            temperature in (optimalTemperatureRange.first - 5)..(optimalTemperatureRange.last + 5) -> 0.5
            temperature in (optimalTemperatureRange.first - 7)..(optimalTemperatureRange.last + 7) -> 0.4
            temperature in (optimalTemperatureRange.first - 8)..(optimalTemperatureRange.last + 8) -> 0.2
            temperature in (optimalTemperatureRange.first - 10)..(optimalTemperatureRange.last + 10) -> 0.12
            else -> 0.08 // 已经匹配到，但是差距过大，无法适应的情况
        }

        Timber.d("temperatureScore overall: $temperatureScore")
        return temperatureScore
    }


    // 计算湿度适应性得分，根据衣物材质和设计
    private fun calculateHumidityScore(humidity: Int, cloth: ClothItem): Double {
        val optimalHumidityRange = when {
            cloth.material.contains("羊毛") -> 35..45 // 羊毛适合中等湿度
            cloth.material.contains("棉") -> 50..60 // 棉适合稍高的湿度
            cloth.material.contains("亚麻") -> 25..35 // 亚麻适合较低的湿度
            cloth.material.contains("丝绸") -> 40..50 // 丝绸适合中等偏高的湿度
            cloth.material.contains("尼龙") -> 35..45 // 尼龙适合中等湿度
            cloth.material.contains("聚酯纤维") -> 40..50 // 聚酯纤维适合中等湿度
            cloth.material.contains("皮革") -> 45..55 // 皮革适合较高的湿度
            cloth.material.contains("羊绒") -> 30..40 // 羊绒适合较低的湿度
            cloth.material.contains("涤纶") -> 40..50 // 涤纶适合中等到较高的湿度
            cloth.material.contains("氨纶") -> 35..45 // 氨纶适合中等湿度
            cloth.material.contains("竹纤维") -> 40..50 // 竹纤维适合中等湿度
            cloth.material.contains("天丝") -> 40..50 // 天丝适合中等偏高的湿度
            else -> {
                Timber.d("material not found: ${cloth.material},return 0.5")
                return 0.5
            } // 匹配不到，则返回中间值，此算法无效。
        }

        val humidityScore = when {
            humidity in optimalHumidityRange -> 1.0
            humidity in (optimalHumidityRange.first - 1)..(optimalHumidityRange.last + 1) -> 0.9
            humidity in (optimalHumidityRange.first - 3)..(optimalHumidityRange.last + 3) -> 0.75
            humidity in (optimalHumidityRange.first - 5)..(optimalHumidityRange.last + 5) -> 0.6
            humidity in (optimalHumidityRange.first - 7)..(optimalHumidityRange.last + 7) -> 0.5
            humidity in (optimalHumidityRange.first - 10)..(optimalHumidityRange.last + 10) -> 0.4
            humidity in (optimalHumidityRange.first - 15)..(optimalHumidityRange.last + 15) -> 0.3
            humidity in (optimalHumidityRange.first - 20)..(optimalHumidityRange.last + 20) -> 0.15
            else -> 0.1 // 已经匹配到，但是差距过大，无法适应的情况
        }

        Timber.d("humidityScore overall: $humidityScore")
        return humidityScore
    }


    // 计算历史穿着偏好得分，考虑最后一次穿着时间
    private fun calculateWearHistoryScore(
        lastWornDate: String,
        sixSigmaInDays: Double = 365.0
    ): Double {
        val lastWornDaysAgo =
            (System.currentTimeMillis() - lastWornDate.toDate().time) / (1000 * 60 * 60 * 24)

        Timber.d("lastWornDaysAgo: $lastWornDaysAgo")

        // 计算标准差 σ
        val sigma = sixSigmaInDays / 6.0

        // 高斯函数参数，均值设为6σ的一半
        val mean = sixSigmaInDays / 2.0

        // 计算高斯函数值
        val score = gauss(lastWornDaysAgo, mean, sigma)
        Timber.d("wearHistoryScore overall: $score")

        return score
    }

    // 高斯函数计算
    private fun gauss(x: Long, mean: Double, sigma: Double): Double {
        return exp(-((x - mean).pow(2.0)) / (2.0 * sigma.pow(2.0)))
    }

    // 字符串转日期
    private fun String.toDate(): Date {
        val format = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
        return format.parse(this) ?: Date()
    }

    // Sigmoid函数，用于非线性处理
    fun customSigmoid(x: Double): Double {
        Timber.d("customSigmoid input: $x")

        val a = 0.8
        val b = 10.0
        val d = 0.2

        // 更新数学表达式以符合新的函数形式
        val result = a / (1 + Math.exp(-b * (2 * x - 1))) + d
        Timber.d("customSigmoid output: $result")

        return result
    }

    // 将评分归一化到5
    private fun scaleToFive(score: Double): Double {
        return score * 5
    }

    /** 二次重映射评分到0-5范围，并进行异常处理。
     *  通过实验发现值域在3.3-4.6之间，故需要二次重映射
     */
    private fun remapScore(score: Double): Double {
        // 假设原始评分范围为3.3-4.6
        val minOriginalScore = 3.3
        val maxOriginalScore = 4.6
        val minNewScore = 0.0
        val maxNewScore = 5.0

        // 线性映射公式
        val remappedScore =
            (score - minOriginalScore) / (maxOriginalScore - minOriginalScore) * (maxNewScore - minNewScore) + minNewScore

        // 异常处理：将超出0-5范围的值限制在0-5范围内
        return when {
            remappedScore < minNewScore -> minNewScore
            remappedScore > maxNewScore -> maxNewScore
            else -> remappedScore
        }
    }

    // 穿衣推荐评分表
    private fun getDressingAdvice(level: Int): String {
        return when (level) {
            0 -> "极热 (32度以上): 真丝：轻薄透气，保持凉爽。建议穿短袖真丝上衣、真丝裙子，配凉鞋。户外活动时注意防晒，佩戴太阳镜和防晒帽。"
            1 -> "极热 (27-32度): 棉质：吸汗透气，保持舒适。建议穿短袖棉质T恤、棉质短裤或短裙。户外活动时佩戴防晒帽，使用防晒霜。"
            2 -> "很热 (25-27度): 长袖衬衫+单裤/长裙，或长袖衬衫裙+短裤，或长袖长裙：适合室内外温差较大的环境，建议穿着轻便透气款，薄的纯棉款式或者真丝材质较好。佩戴防晒帽或使用遮阳伞，防止阳光直射。"
            3 -> "热 (20-25度): 长袖衬衫，薄外套，单裤，防晒帽：适合早晚温差较大的季节。薄外套可随时脱下，防晒帽用于户外防晒。"
            4 -> "温暖 (15-20度): 风衣，厚衬衫+毛背心，或薄衬衫+毛衣，加绒裤，薄帽子：适合初秋或初春，风衣防风保暖。建议选择轻便、保暖的服装，防止受凉。"
            5 -> "凉爽 (10-15度): 大衣，打底，毛衣，加绒裤，秋裤，围巾，薄帽子，纱巾：适合深秋或早春，建议多层次穿衣，方便根据气温变化增减衣物。"
            6 -> "冷 (5-10度): 羽绒服或长款大衣，打底，毛衣，秋裤，加绒裤，厚袜子，厚帽子，厚围巾：适合寒冷天气，羽绒服保暖效果好，建议选择防风、防寒的服装。"
            7 -> "寒冷 (0-5度): 长款羽绒服，打底，厚毛衣或长毛摇粒绒，厚打底裤，加绒裤，厚袜子，加绒鞋，护耳毛线帽，厚围巾：适合严冬天气，注意保暖，防止冻伤。"
            8 -> "极冷 (0度以下): 长款羽绒服，厚打底，厚毛衣或长毛摇粒绒，鲨鱼裤+夹层裤，厚袜子，加绒鞋，护耳毛线帽，厚围巾，进一步降温时可加羽绒马甲：适合极寒天气，穿戴多层次，确保保暖。"
            else -> "无效的等级。"
        }
    }
}
