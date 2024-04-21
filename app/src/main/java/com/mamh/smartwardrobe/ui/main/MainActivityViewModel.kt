package com.mamh.smartwardrobe.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mamh.smartwardrobe.bean.flag.MessageType
import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.bean.item.DataItem
import com.mamh.smartwardrobe.data.AppRepository
import com.mamh.smartwardrobe.data.serialize.DatagramParser
import com.mamh.smartwardrobe.data.serialize.SensorData
import com.mamh.smartwardrobe.util.itembuild.DataItemBuilder
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository


    //数据接收缓存
    private val _dataReceiveCache = _repository.dataReceiveCache
    val dataReceiveCache: LiveData<String>
        get() = _dataReceiveCache

    //数据发送缓存
    private val _dataSendCache = _repository.dataSendCache
    val dataSendCache: LiveData<String>
        get() = _dataSendCache

    //最新一个item的的时间戳，用来防止重复发送数据
    private val _latestItemTimestamp = MutableLiveData<Long>().apply {
        value = 0
    }
    val latestItemTimestamp: LiveData<Long>
        get() = _latestItemTimestamp

    //发送和接受数据的列表
    private val _dataList = repository.dataList
    var dataList: LiveData<MutableList<DataItem>> = _dataList


    //发送数据到已连接的主机。注意：该方法为网络方法，要在网络进程中处理
    fun sendDataToServer() {
        viewModelScope.launch {
            repository.sendDataToServer()
        }
    }

    //将接收和发送的数据存入列表
    fun addDataToDataList(dataItem: DataItem) {
        repository.addDataItemToList(dataItem)
    }

    //修改发送缓存，相当于去直接发送数据
    fun modifySendCache(data: String) {
        repository.setDataSendCache(data)
    }

    //修改发送时间，避免重新发起数据发送请求
    fun modifyLatestTimestamp(time: Long) {
        _latestItemTimestamp.postValue(time)
        //Timber.d("修改发送时间$time")
    }

    /// 解析完整的数据报文
    fun parseDatagram(datagram: String) {
        // 解析数据报文
        val parser: DatagramParser = DatagramParser.Builder()
            .setParseType(DatagramParser.ParseType.SENSOR_DATA)
            .build()
        when (val sensorData = parser.parse(datagram)) {
            is SensorData -> {
                // 处理传感器数据
                // 更新温度
                repository.updateTemperature(sensorData.temperature.toFloat())
                // 更新亮度
                repository.updateBrightness(sensorData.brightness)
                // 更新灯光开度
                repository.updateLightOpenness(sensorData.lightOpenness)
                // 更新湿度
                repository.updateHumidity(sensorData.humidity)
                // 更新测量时间
                repository.updateMeasureTime(sensorData.measureTime)
                // 更新旋钮值
                repository.updateKnobValue(sensorData.knobValue)
            }
        }
    }

    // 获取以秒做单位的时间戳
    private fun getTime(): Int {
        val timeStamp = System.currentTimeMillis()
        val timeStampSec = (timeStamp / 1000).toInt()
        Timber.d("时间戳$timeStampSec")
        return timeStampSec
    }

    //向目标主机发送数据，调用这个函数
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