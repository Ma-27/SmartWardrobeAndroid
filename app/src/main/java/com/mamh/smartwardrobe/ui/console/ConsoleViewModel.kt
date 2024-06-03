package com.mamh.smartwardrobe.ui.console

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamh.smartwardrobe.bean.flag.MessageType
import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.data.AppRepository
import com.mamh.smartwardrobe.data.database.SmartWardrobeDatabase
import com.mamh.smartwardrobe.data.database.weather.WeatherDao
import com.mamh.smartwardrobe.util.itembuild.DataItemBuilder

class ConsoleViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository

    // 获取数据库中天气表实例
    private val weatherDao: WeatherDao = SmartWardrobeDatabase.getInstance(application).weatherDao

    /// ------------------------- 其他网络API数据和服务数据部分 --------------------------------------------------------
    // 经纬度信息，默认值为空字符串
    private val _latlng = MutableLiveData<String>("")
    val latlng: LiveData<String>
        get() = _latlng

    // 天气信息
    private val _usefulDailyWeatherDetail = _repository.usefulDailyWeatherDetail
    val usefulDailyWeatherDetail: LiveData<UsefulDailyWeatherDetail>
        get() = _usefulDailyWeatherDetail


    /// ---------------------------- 传感器数据部分 --------------------------------------------------------
    // 缓存温度调节的目标温度
    private val _targetTemperature = MutableLiveData<Int>().apply {
        value = 15
    }
    val targetTemperature: LiveData<Int>
        get() = _targetTemperature


    //缓存温度调节的当前温度
    private val _currentTemperature = _repository.currentTemperature
    val currentTemperature: LiveData<Float>
        get() = _currentTemperature

    //缓存湿度调节的当前湿度
    private val _currentHumidity = _repository.currentHumidity
    val currentHumidity: LiveData<Int>
        get() = _currentHumidity

    // 缓存温度调节的目标湿度
    private val _targetHumidity = MutableLiveData<Int>().apply {
        value = 21
    }
    val targetHumidity: LiveData<Int>
        get() = _targetHumidity

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
    fun setTargetTemperature(temperature: Int) {
        _targetTemperature.value = temperature
    }

    // 设置目标湿度
    fun setTargetHumidity(humidity: Int) {
        _targetHumidity.value = humidity
    }

    // Setter 方法，允许外部更新LiveData的值


    // 设置穿衣建议
    fun setWeatherDetail(value: UsefulDailyWeatherDetail) {
        repository.setUsefulDailyWeatherDetail(value)
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