package com.mamh.smartwardrobe.data.serialize

import com.mamh.smartwardrobe.data.AppRepository
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


/**
@description DatagramParser 类用于解析来自硬件设备或服务器的数据
@author Mamh
@Date 2024/4/21
 **/

class DatagramParser private constructor(private val parseType: ParseType) {
    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()

    enum class ParseType {
        SENSOR_DATA,
        SERVER_RESPONSE
    }

    class Builder {
        private var parseType: ParseType = ParseType.SENSOR_DATA

        fun setParseType(parseType: ParseType): Builder {
            this.parseType = parseType
            return this
        }

        fun build(): DatagramParser {
            return DatagramParser(parseType)
        }
    }

    /**
     * 解析JSON字符串，根据 parseType 返回不同类型的数据。
     * @param jsonString 待解析的JSON字符串。
     * @return 解析后的数据，可能是 SensorData 或 ServerResponse。
     */
    fun parse(jsonString: String): Any {
        return when (parseType) {
            ParseType.SENSOR_DATA -> parseSensorData(jsonString)
            ParseType.SERVER_RESPONSE -> parseServerResponse(jsonString)
        }
    }

    private fun parseSensorData(jsonString: String): SensorData {
        val jsonObject = try {
            JSONObject(jsonString)
        } catch (e: JSONException) {
            Timber.e("Error parsing JSON: ${e.localizedMessage}")
            // 从ViewModel或Repository获取默认值
            return createDefaultSensorData()
        }

        val dataObject = jsonObject.getJSONObject("data")
        val dataStreams = dataObject.getJSONArray("datastreams")

        var temperature: Float = _repository.currentTemperature.value ?: 0.0f
        var brightness: Int = _repository.currentBrightness.value ?: 0
        var lightOpenness: Int = _repository.currentLightOpenness.value ?: 0
        var humidity: Int = _repository.currentHumidity.value ?: 0
        var measureTime: Long = _repository.currentMeasureTime.value ?: 0L
        var knobValue: Int = _repository.currentKnobValue.value ?: 0

        for (i in 0 until dataStreams.length()) {
            val stream = dataStreams.getJSONObject(i)
            val id = stream.getString("id")
            val dataPoints = stream.getJSONArray("datapoints")
            val dataPoint = dataPoints.getJSONObject(0)
            if (dataPoint.isNull("value")) continue

            val value = dataPoint.optString("value", null)
            if (value == null) continue  // 确保非空才进行解析

            try {
                when (id) {
                    "Temperature" -> temperature = value.toFloatOrNull() ?: temperature
                    "Brightness" -> brightness = value.toIntOrNull() ?: brightness
                    "LightOpenness" -> lightOpenness = value.toIntOrNull() ?: lightOpenness
                    "Humidity" -> humidity = value.toIntOrNull() ?: humidity
                    "MeasureTime" -> measureTime = value.toLongOrNull() ?: measureTime
                    "KnobValue" -> knobValue = value.toIntOrNull() ?: knobValue
                }
            } catch (e: NumberFormatException) {
                Timber.e("Error parsing dataStream for ID: $id, error: ${e.localizedMessage}")
                _repository.setUserHint("设备不在线，有个别数据未更新")
            }
        }

        return SensorData(temperature, brightness, lightOpenness, humidity, measureTime, knobValue)
    }

    private fun createDefaultSensorData(): SensorData {
        // 从ViewModel或Repository获取默认值
        return SensorData(
            _repository.currentTemperature.value ?: 0.0f,
            _repository.currentBrightness.value ?: 0,
            _repository.currentLightOpenness.value ?: 0,
            _repository.currentHumidity.value ?: 0,
            _repository.currentMeasureTime.value ?: 0L,
            _repository.currentKnobValue.value ?: 0
        )
    }



    /**
     * 解析服务器响应的函数。
     * @param jsonString 从服务器接收到的JSON字符串。
     * @return ServerResponse 实例，表示解析后的响应结果。
     */
    private fun parseServerResponse(jsonString: String): ServerResponse {
        val jsonObject = JSONObject(jsonString)
        // 默认为 -1，如果找不到 "errno"
        val errorCode = jsonObject.optInt("errno", -1)

        return when (errorCode) {
            // 没有错误，继续检查数据
            0 -> {
                val dataObject = jsonObject.optJSONObject("data")
                if (dataObject != null) {
                    val cmdUuid = dataObject.optString("cmd_uuid", "")
                    if (cmdUuid.isNotEmpty()) {
                        // 成功响应，返回包含UUID的成功对象
                        ServerResponse.Success(cmdUuid)
                    } else {
                        // 数据未找到
                        ServerResponse.DataNotFound
                    }
                } else {
                    // 数据未找到
                    ServerResponse.DataNotFound
                }
            }

            // 没有找到 "errno"，表明JSON格式错误或未预期的格式
            -1 -> ServerResponse.DataNotFound
            else -> {
                // 处理错误情况
                val errorMessage = jsonObject.optString("error", "Unknown Mistake")
                // 错误响应，返回错误码和错误信息
                ServerResponse.Error(errorCode, errorMessage)
            }
        }
    }
}
