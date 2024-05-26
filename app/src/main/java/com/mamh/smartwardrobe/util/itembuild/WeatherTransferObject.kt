package com.mamh.smartwardrobe.util.itembuild


/**
@description
@author Mamh
@Date 2024/5/26
 **/

class WeatherTransferObject {
    companion object {
        // 将天气编码转化为具体的天气现象
        fun convertToPhenomenon(weatherCode: String): String {
            return when (weatherCode) {
                "CLEAR_DAY" -> "晴朗"
                "CLEAR_NIGHT" -> "晴夜"
                "PARTLY_CLOUDY_DAY" -> "多云"
                "PARTLY_CLOUDY_NIGHT" -> "多云夜"
                "CLOUDY" -> "阴"
                "LIGHT_HAZE" -> "轻度雾霾"
                "MODERATE_HAZE" -> "中度雾霾"
                "HEAVY_HAZE" -> "重度雾霾"
                "LIGHT_RAIN" -> "小雨"
                "MODERATE_RAIN" -> "中雨"
                "HEAVY_RAIN" -> "大雨"
                "STORM_RAIN" -> "暴雨"
                "FOG" -> "雾"
                "LIGHT_SNOW" -> "小雪"
                "MODERATE_SNOW" -> "中雪"
                "HEAVY_SNOW" -> "大雪"
                "STORM_SNOW" -> "暴雪"
                "DUST" -> "浮尘"
                "SAND" -> "沙尘"
                else -> "天气"
            }
        }


        // 将穿衣指数转化为具体的穿衣建议
        fun convertToDressingAdvice(dressingIndex: Int): String {
            return when (dressingIndex) {
                0 -> "极热，建议穿着真丝衣物，保持透气舒适"
                1 -> "极热，建议穿着真丝衣物，保持透气舒适"
                2 -> "很热，建议穿着轻薄的棉质衣物，保持透气性"
                3 -> "热，建议穿着透气性好的衣物，如短袖衬衫、T恤等"
                4 -> "温暖，穿着舒适，可适当增加衣物层次，如毛衣、薄外套等"
                5 -> "凉爽，穿着轻薄的外套，如风衣，搭配长裤"
                6 -> "冷，穿着厚实的外套，如羽绒服，搭配毛衣和长裤"
                7 -> "寒冷，穿着厚重的保暖衣物，如羽绒服、厚毛衣等"
                8 -> "极冷，穿着极厚的保暖衣物，如羽绒服、厚毛衣等"
                else -> "穿衣指数"
            }
        }

        // 舒适度指数描述
        fun convertToComfortDescription(comfortIndex: Int): String {
            return when (comfortIndex) {
                0 -> "闷热如蒸笼"
                1 -> "仿佛身处沙漠中"
                2 -> "空气中弥漫着微热的气息"
                3 -> "热的让人喘不过气来"
                4 -> "暖阳洒落，微风拂面"
                5 -> "仿佛置身于春日暖阳下"
                6 -> "微风轻拂，清爽宜人"
                7 -> "风寒刺骨，冷飕飕的"
                8 -> "冻得人直打哆嗦"
                9 -> "冰冻的空气,呼出的一片云雾"
                10 -> "冻得人感受不到自己的身体"
                11 -> "冻得人感觉连骨头都在颤抖"
                12 -> "湿冷透骨，冰雨霏霏"
                13 -> "干冷刺骨，寒风呼啸"
                else -> "舒适度指数"
            }
        }
    }
}