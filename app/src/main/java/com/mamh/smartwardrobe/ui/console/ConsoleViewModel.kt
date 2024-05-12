package com.mamh.smartwardrobe.ui.console

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamh.smartwardrobe.bean.flag.MessageType
import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.data.AppRepository
import com.mamh.smartwardrobe.util.itembuild.DataItemBuilder
import timber.log.Timber

class ConsoleViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository

    /// ------------------------- 其他网络API数据和服务数据部分 --------------------------------------------------------
    // 经纬度信息，默认值为空字符串
    private val _latlng = MutableLiveData<String>("")
    val latlng: LiveData<String>
        get() = _latlng

    // 空气湿度，默认值为
    private val _airHumidity = MutableLiveData<Int>(0)
    val airHumidity: LiveData<Int>
        get() = _airHumidity

    // PM指数，默认值为0
    private val _pmIndex = MutableLiveData<Int>(0)
    val pmIndex: LiveData<Int>
        get() = _pmIndex

    // 温度，默认值为18°C
    private val _weatherForecastTemperature = MutableLiveData<Int>(18)
    val weatherForecastTemperature: LiveData<Int>
        get() = _weatherForecastTemperature

    // 位置，默认值为"Unknown location"
    private val _location = MutableLiveData<String>("Unknown location")
    val location: LiveData<String>
        get() = _location

    // 天气状况，默认值为"Sunny"
    private val _weatherCondition = MutableLiveData<String>("Sunny")
    val weatherCondition: LiveData<String>
        get() = _weatherCondition

    // 穿衣建议，默认值为"Standard attire"
    private val _clothingSuggestion = MutableLiveData<String>("Standard attire")
    val clothingSuggestion: LiveData<String>
        get() = _clothingSuggestion


    /// ---------------------------- 传感器数据部分 --------------------------------------------------------
    // 缓存温度调节的目标温度
    private val _targetTemperature = _repository.targetTemperature
    val targetTemperature: LiveData<Int>
        get() = _targetTemperature

    // 滑动时可见的目标温度，当结束滑动时，设置为目标温度
    private val _pendingTemperature = MutableLiveData<Int>().apply {
        value = 15
    }
    val pendingTemperature: LiveData<Int>
        get() = _pendingTemperature


    //缓存温度调节的当前温度
    private val _currentTemperature = _repository.currentTemperature
    val currentTemperature: LiveData<Float>
        get() = _currentTemperature

    //缓存湿度调节的当前湿度
    private val _currentHumidity = _repository.currentHumidity
    val currentHumidity: LiveData<Int>
        get() = _currentHumidity

    //缓存灯光是否开启
    private val _lightOn = _repository.lightOn
    val lightOn: LiveData<Boolean>
        get() = _lightOn

    // 灯光是否自动控制
    private val _lightControlAuto = _repository.lightControlAuto
    val lightControlAuto: LiveData<Boolean>
        get() = _lightControlAuto


    // 温度控制是否自动
    private val _temperatureControlAuto = MutableLiveData<Boolean>().apply {
        value = false
    }
    val temperatureControlAuto: LiveData<Boolean>
        get() = _temperatureControlAuto


    /// ---------------------------- setter部分 --------------------------------------------------------

    // 设置目标温度
    fun setTargetTemperature() {
        Timber.d("设置目标温度为${pendingTemperature.value}")
        pendingTemperature.value?.let { _repository.setTargetTemperature(it) }
    }

    // 设置目标温度
    fun setPendingTemperature(temperature: Int) {
        _pendingTemperature.value = temperature
    }

    // Setter 方法，允许外部更新LiveData的值
    fun setHumidity(value: Int) {
        _airHumidity.value = value
    }

    fun setPmIndex(value: Int) {
        _pmIndex.value = value
    }

    fun setTemperature(value: Int) {
        _weatherForecastTemperature.value = value
    }

    fun setLocation(value: String) {
        _location.value = value
    }

    fun setWeatherCondition(value: String) {
        _weatherCondition.value = value
    }

    fun setClothingSuggestion(value: String) {
        _clothingSuggestion.value = value
    }


    /// ---------------------------- 处理逻辑 --------------------------------------------------------
    //向目标主机发送数据
    fun sendData(data: String) {
        //构建数据对象
        val dataItem = DataItemBuilder.buildDataItem(data)
        dataItem.messageType = MessageType.MESSAGE_SEND
        dataItem.messageStatus = TransmissionStatus.UNKNOWN
        dataItem.event = DataItemBuilder.determineEventString(data, MessageType.MESSAGE_SEND)
        //存入发送数据列表中，发送数据
        repository.addDataItemToList(dataItem)
    }

}