package com.mamh.smartwardrobe.network.iot

import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.data.AppRepository
import timber.log.Timber


class IotTransmissionImpl : IotTransmission {
    lateinit var dataIn: String
    lateinit var dataOut: String
    private val repository by lazy {
        AppRepository.Builder()
            .setInternetMode()
            .build()
    }

    //当接收到数据时，在此处理。FIXME 此处可能有socket空异常
    override suspend fun onReceiveData() {

    }

    //当发送数据时，在此处理。FIXME 此处可能有socket空异常
    override suspend fun onSendData(): Int {


        //返回传输结果
        return TransmissionStatus.SUCCESS
    }

    //获取以秒做单位的时间戳
    private fun getTime(): Int {
        val timeStamp = System.currentTimeMillis()
        val timeStampSec = (timeStamp / 1000).toInt()
        Timber.d("时间戳$timeStampSec")
        return timeStampSec
    }
}