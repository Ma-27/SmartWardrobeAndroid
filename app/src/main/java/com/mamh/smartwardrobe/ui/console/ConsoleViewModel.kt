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


    //缓存温度调节的目标温度
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

    // 设置目标温度
    fun setTargetTemperature() {
        Timber.d("设置目标温度为${pendingTemperature.value}")
        pendingTemperature.value?.let { _repository.setTargetTemperature(it) }
    }

    // 设置目标温度
    fun setPendingTemperature(temperature: Int) {
        _pendingTemperature.value = temperature
    }

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