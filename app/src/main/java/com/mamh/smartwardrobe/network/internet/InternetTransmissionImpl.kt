package com.mamh.smartwardrobe.network.internet

import com.chinamobile.iot.onenet.OneNetApi
import com.chinamobile.iot.onenet.OneNetApiCallback
import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.data.AppRepository
import com.mamh.smartwardrobe.data.serialize.DatagramParser
import com.mamh.smartwardrobe.data.serialize.ServerResponse
import com.mamh.smartwardrobe.util.Configuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.CountDownLatch


/**
@description
@author Mamh
@Date 2024/4/19
 **/

class InternetTransmissionImpl : InternetTransmission {
    lateinit var dataIn: String
    lateinit var dataOut: String
    private val repository by lazy {
        AppRepository.Builder()
            .setInternetMode()
            .build()
    }

    // 这里必须要使用TransmissionStatus.CONST 这种的状态码，否则会因为不兼容而保存
    var status = TransmissionStatus.UNKNOWN

    private lateinit var latch: CountDownLatch


    override suspend fun onReceiveData() {
        if (repository.getNewDataQuery()) {
            // todo
            // 这里可能根据不同的报文类型发送不同的数据。下面仅仅请求传感器数据
            OneNetApi.queryDataPoints(
                Configuration.deviceID,
                null,
                ReceiveDataCallback()
            )
            repository.setNewDataQuery(false)
        }
    }

    override suspend fun onSendData(): Int {
        dataOut = repository.dataSendCache.value.toString()
        latch = CountDownLatch(1)  // 初始化 CountDownLatch 为 1

        // todo
        // 这里可能根据不同的报文类型发送不同的数据。这里仅仅发送raw data给arduino系统。云平台原样将报文转发给设备
        OneNetApi.sendCmdToDevice(
            Configuration.deviceID,
            dataOut,
            SendDataCallback()
        )
        withContext(Dispatchers.IO) {
            latch.await()
        }  // 等待所有操作完成
        Timber.d("Send Data status: $status")
        Timber.d("Send Data: $dataOut")
        return status
    }


    private inner class ReceiveDataCallback : OneNetApiCallback {
        override fun onSuccess(response: String) {
            repository.setDataReceiveCache(response)  // 直接访问外部类的data
            Timber.d("Successfully receiving Data point: $response")
            status = TransmissionStatus.SUCCESS
        }

        override fun onFailed(e: Exception) {
            Timber.e(e, "Failed to query data points")
            status = TransmissionStatus.FAIL
        }
    }

    private inner class SendDataCallback : OneNetApiCallback {
        override fun onSuccess(response: String) {
            Timber.d("Successfully sending Data, here is the response from server: $response")
            // 解析发送结果Json
            try {
                // 解析数据报文
                val parser: DatagramParser = DatagramParser.Builder()
                    .setParseType(DatagramParser.ParseType.SERVER_RESPONSE)
                    .build()

                when (val returnData = parser.parse(response)) {
                    is ServerResponse.Success -> {
                        // 服务器成功处理命令
                        status = TransmissionStatus.SUCCESS
                    }

                    is ServerResponse.Error -> {
                        // 根据错误码设置不同的状态
                        status = when (returnData.errorCode) {
                            // 设备不在线
                            10 -> {
                                TransmissionStatus.DEVICE_NOT_ONLINE
                            }

                            else -> TransmissionStatus.FAIL
                        }
                    }

                    is ServerResponse.DataNotFound -> {
                        // 数据未找到
                        status = TransmissionStatus.INVALID_JSON
                    }

                    else -> {
                        // 其他未知响应
                        status = TransmissionStatus.UNKNOWN


                    }
                }
            } finally {
                latch.countDown()  // 减少计数器，表示操作完成
            }

        }

        override fun onFailed(e: Exception) {
            try {
                Timber.e(e, "Failed to send data points")
                status = TransmissionStatus.FAIL
            } finally {
                latch.countDown()  // 减少计数器，表示操作完成
            }
        }
    }
}