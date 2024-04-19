package com.mamh.smartwardrobe.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamh.smartwardrobe.bean.flag.TestFlag
import com.mamh.smartwardrobe.bean.item.DataItem
import com.mamh.smartwardrobe.network.Connection
import com.mamh.smartwardrobe.network.NetWorkServiceFactory
import com.mamh.smartwardrobe.network.Transmission

/*
app repository是整个app的数据中心，所有app的全局数据都暂存在这里。
同时，该类负责处理多线程问题，比如数据库存取和网络请求。同时也是多线程任务的管理中心
 */
//物联网模块的连接处理对象
//物联网模块的数据发送处理对象
class AppRepository(
    val iotConnection: Connection?,
    val iotTransmission: Transmission?
) {
    companion object {
        //volatile注释不允许缓存该变量
        @Volatile
        private var INSTANCE: AppRepository? = null

        //App repository的实例化方法，采用单例模式，整个app只允许存在一个app repository实例（懒汉饿汉、线程安全）
        fun getInstance(): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Builder().build().also { INSTANCE = it }
            }
        }

        fun getInstanceForIot(
            connection: Connection,
            transmission: Transmission
        ): AppRepository {
            // FIXME
            val iotConnection = NetWorkServiceFactory.buildIotConnectionService()
            val iotTransmission = NetWorkServiceFactory.buildIotTransmissionService()

            return INSTANCE ?: synchronized(this) {
                // builder模式，设置为iot模式，通过自己内部的方法创建网络连接和数据传输对象
                INSTANCE ?: Builder().setIotMode(connection, transmission).build()
                    .also { INSTANCE = it }
            }
        }

        fun getInstanceForInternet(): AppRepository {
            return INSTANCE ?: synchronized(this) {
                // builder模式，设置为internet模式，通过自己内部的方法创建网络连接和数据传输对象
                INSTANCE ?: Builder().setInternetMode().build().also { INSTANCE = it }
            }
        }
    }


    /**
     * 通过 Builder 模式构建 AppRepository 实例。当传入 IoT 参数时，表示使用 IoT 模式，否则使用互联网模式。它们的网络连接实现手段不同。
     */
    class Builder {
        private var connection: Connection? = null
        private var transmission: Transmission? = null

        // 可选build模式为iot模式，这个时候Wi-Fi接入点即为服务端，APP充当客户端，收发数据通过本地Wi-Fi进行。
        fun setIotMode(iotConnection: Connection, iotTransmission: Transmission) = apply {
            this.connection = NetWorkServiceFactory.buildIotConnectionService()
            this.transmission = NetWorkServiceFactory.buildIotTransmissionService()
        }

        // 可选build模式为Internet模式，这个时候APP接入云平台，收发数据通过互联网进行，APP充当客户端，设置 IoT 模式
        fun setInternetMode() = apply {
            this.connection = NetWorkServiceFactory.buildInternetConnectionService()
            this.transmission = NetWorkServiceFactory.buildInternetTransmissionService()
        }

        fun build(): AppRepository {
            return INSTANCE ?: AppRepository(connection, transmission)
        }
    }


    /**
     * --------------------------------------------------------------------------------------------------------------------------------------------------------
     * 这里正式进入存储数据的部分
     */

    /// SnackBar提示字符串变量
    private val _networkStatus = MutableLiveData<String>().apply {
        value = "向下滑动来刷新WLAN列表"
    }
    val networkStatus: LiveData<String>
        get() = _networkStatus


    //数据中心中显示的数据列表
    //为了应对set value 导致的list同步问题，如果需要更改该list，需要同步更改mdataList和dataList
    private val mdataList: MutableList<DataItem> = ArrayList()
    private val _dataList = MutableLiveData<MutableList<DataItem>>().apply {
        value = mdataList
    }
    var dataList: LiveData<MutableList<DataItem>> = _dataList

    //要接收的数据缓存（相比起数据列表更容易access）
    private val _dataReceiveCache = MutableLiveData<String>().apply {
        //默认值为TestFlag.RECEIVE
        value = TestFlag.RECEIVE
    }
    val dataReceiveCache: LiveData<String>
        get() = _dataReceiveCache

    //要发送的数据缓存（相比起数据列表更容易access）
    private val _dataSendCache = MutableLiveData<String>().apply {
        //默认值为TestFlag.SEND
        value = TestFlag.SEND
    }
    val dataSendCache: LiveData<String>
        get() = _dataSendCache

    //缓存温度调节的当前温度
    private val _currentTemperature = MutableLiveData<Float>().apply {
        value = 13.8f
    }
    val currentTemperature: LiveData<Float>
        get() = _currentTemperature


    //缓存温度调节的目标温度
    private val _targetTemperature = MutableLiveData<Int>().apply {
        value = 15
    }
    val targetTemperature: LiveData<Int>
        get() = _targetTemperature

    // 温度控制是否自动
    private val _temperatureControlAuto = MutableLiveData<Boolean>().apply {
        value = false
    }
    val temperatureControlAuto: LiveData<Boolean>
        get() = _temperatureControlAuto

    fun setTemperatureControlAuto(value: Boolean) {
        _temperatureControlAuto.value = value
    }

    // 设置目标温度
    fun setTargetTemperature(temperature: Int) {
        _targetTemperature.value = temperature
    }
}