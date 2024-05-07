package com.mamh.smartwardrobe.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamh.smartwardrobe.bean.flag.TestFlag
import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.bean.item.DataItem
import com.mamh.smartwardrobe.bean.item.WifiItem
import com.mamh.smartwardrobe.network.Connection
import com.mamh.smartwardrobe.network.NetWorkServiceFactory
import com.mamh.smartwardrobe.network.Transmission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
app repository是整个app的数据中心，所有app的全局数据都暂存在这里。
同时，该类负责处理多线程问题，比如数据库存取和网络请求。同时也是多线程任务的管理中心
 */
//物联网模块的连接处理对象
//物联网模块的数据发送处理对象
class AppRepository private constructor(
    var connection: Connection?,
    var transmission: Transmission?
) {
    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(null, null).also {
                    INSTANCE = it
                }
            }
        }
    }


    /**
     * 通过 Builder 模式构建 AppRepository 实例。当传入 IoT 参数时，表示使用 IoT 模式，否则使用互联网模式。它们的网络连接实现手段不同。
     */
    class Builder {
        private var connection: Connection? = null
        private var transmission: Transmission? = null

        fun setIotMode(iotConnection: Connection, iotTransmission: Transmission) = apply {
            this.connection = NetWorkServiceFactory.buildIotConnectionService()
            this.transmission = NetWorkServiceFactory.buildIotTransmissionService()
        }

        fun setInternetMode() = apply {
            this.connection = NetWorkServiceFactory.buildInternetConnectionService()
            this.transmission = NetWorkServiceFactory.buildInternetTransmissionService()
        }

        fun build(): AppRepository {
            synchronized(AppRepository::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = AppRepository(connection, transmission)
                }
            }
            return INSTANCE!!
        }
    }


    /**
     * --------------------------------------------------------------------------------------------------------------------------------------------------------
     * 这里正式进入存储数据的部分
     */

    /// SnackBar提示字符串变量
    private val _userHint = MutableLiveData<String>().apply {
        value = "Welcome to smart wardrobe!"
    }
    val userHint: LiveData<String>
        get() = _userHint

    /// 标记是否有新的刷新命令
    private var newDataQuery = false


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


    // 存储当前亮度的数据
    private val _currentBrightness = MutableLiveData<Int>().apply {
        value = 0 // 初始化值
    }
    val currentBrightness: LiveData<Int>
        get() = _currentBrightness

    // 存储当前灯光开度的数据
    private val _currentLightOpenness = MutableLiveData<Int>().apply {
        value = 0 // 初始化值
    }
    val currentLightOpenness: LiveData<Int>
        get() = _currentLightOpenness

    // 存储当前湿度的数据
    private val _currentHumidity = MutableLiveData<Int>().apply {
        value = 0 // 初始化值
    }
    val currentHumidity: LiveData<Int>
        get() = _currentHumidity

    // 存储当前测量时间的数据
    private val _currentMeasureTime = MutableLiveData<Long>().apply {
        value = 0L // 初始化值
    }
    val currentMeasureTime: LiveData<Long>
        get() = _currentMeasureTime

    // 存储当前旋钮值的数据
    private val _currentKnobValue = MutableLiveData<Int>().apply {
        value = 0 // 初始化值
    }
    val currentKnobValue: LiveData<Int>
        get() = _currentKnobValue


    // 灯光是否开启
    private val _lightOn = MutableLiveData<Boolean>().apply {
        value = false
    }
    val lightOn: LiveData<Boolean>
        get() = _lightOn

    // 灯光控制是否自动
    private val _lightControlAuto = MutableLiveData<Boolean>().apply {
        value = false
    }
    val lightControlAuto: LiveData<Boolean>
        get() = _lightControlAuto


    //-------------------各自字段的setter方法--------------------------------------------------------------------------------------------------------------------------
    fun setNewDataQuery(value: Boolean) {
        newDataQuery = value
    }

    fun getNewDataQuery(): Boolean {
        return newDataQuery
    }

    // 设置目标温度
    fun setTargetTemperature(temperature: Int) {
        _targetTemperature.value = temperature
    }


    fun setUserHint(value: String) {
        _userHint.postValue(value)
    }

    //收到新的温度数据后，设置室温
    fun setCurrentTemperature(temperature: Float) {
        _currentTemperature.postValue(temperature)
    }

    // 更新温度数据的方法
    fun updateTemperature(temperature: Float) {
        _currentTemperature.value = temperature
    }

    // 更新亮度数据的方法
    fun updateBrightness(brightness: Int) {
        _currentBrightness.value = brightness
    }

    // 更新灯光开度数据的方法
    fun updateLightOpenness(openness: Int) {
        _currentLightOpenness.value = openness
    }

    // 更新湿度数据的方法
    fun updateHumidity(humidity: Int) {
        _currentHumidity.value = humidity
    }

    // 更新测量时间数据的方法
    fun updateMeasureTime(measureTime: Long) {
        _currentMeasureTime.value = measureTime
    }

    // 更新旋钮值数据的方法
    fun updateKnobValue(knobValue: Int) {
        _currentKnobValue.value = knobValue
    }


// ----------------------------------------------------------------


    //更改接收缓存，提示收到数据了
    fun setDataReceiveCache(data: String) {
        _dataReceiveCache.postValue(data)
        //Timber.d("接收缓存收到数据$data")
    }

    //更改发送缓存，发送缓存收到更改后，应安排发送数据
    fun setDataSendCache(data: String) {
        _dataSendCache.postValue(data)
    }

    //在数据中心的数据列表中添加数据
    // 为了应对线程不同步问题，先从mdataList中添加元素，再将修改后的整个list传递给datalist
    fun addDataItemToList(dataItem: DataItem) {
        mdataList.add(dataItem)
        _dataList.postValue(mdataList)
    }


    /***
     * 预留其他类access该类并修改数据的接口，通常从view model类中access此类
     */
    //检查网络状态
    fun checkNetWorkState() {
        //调用网络模块进行刷新
        connection?.checkNetworkState()
    }

    //连接到指定wifi，wifiItem中含有wifi参数
    fun connectToSpecifiedWifi(item: WifiItem) {
        connection?.connect(item)
    }

    //断开与现在的wifi的连接
    fun disConnectWifi() {
        connection?.disconnect()
    }

    //提醒repository，设备已经接收到网络发来的数据了
    suspend fun notifyDataReceiving() {
        /// 启动一个新的接收数据线程
        withContext(Dispatchers.IO) {
            transmission?.onReceiveData()
        }
    }

    //提醒repository socket已经改变
    suspend fun notifySocketChanged() {
        //在本app中，socket是连接的最后一步，如果成功生成或者更改了socket，则可以监听数据发送，这里需要提示刷新数据
        notifyDataReceiving()
    }

    //向目标主机发送数据
    suspend fun sendDataToServer() {
        withContext(Dispatchers.IO) {
            //发送数据
            val status = transmission?.onSendData()
            //报告发送状态
            setUserHint(
                when (status) {
                    TransmissionStatus.SUCCESS -> "数据发送成功"
                    TransmissionStatus.FAIL -> "数据发送失败"
                    TransmissionStatus.SOCKET_NULL -> "端口为空"
                    TransmissionStatus.UNCONNECTED -> "网络未连接，请连接网络时再试"
                    TransmissionStatus.DEVICE_NOT_ONLINE -> "设备不在线，请检查设备的电源或网络"
                    TransmissionStatus.INVALID_JSON -> "服务器似乎返回了无效的数据"
                    TransmissionStatus.UNKNOWN -> "未知的网络请求状态"
                    else -> {
                        "未知的网络请求状态"
                    }
                }
            )
        }
    }
}