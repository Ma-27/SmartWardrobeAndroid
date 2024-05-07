package com.mamh.smartwardrobe.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.mamh.smartwardrobe.SmartWardrobeApplication
import com.mamh.smartwardrobe.bean.flag.NetWorkStatic.Companion.IOT_WLAN_CONNECTION_SERVICE
import com.mamh.smartwardrobe.bean.flag.NetWorkStatic.Companion.IOT_WLAN_TRANSMISSION_SERVICE
import com.mamh.smartwardrobe.network.internet.InternetConnectionImpl
import com.mamh.smartwardrobe.network.internet.InternetTransmissionImpl
import com.mamh.smartwardrobe.network.iot.IotConnectionImpl
import com.mamh.smartwardrobe.network.iot.IotTransmissionImpl


//工厂方法，通过选择不同的网络类型返回不同的网络服务实例，屏蔽掉context和网络接口的具体实现
class NetWorkServiceFactory {
    companion object {
        //volatile注释不允许缓存该变量
        @Volatile
        private var INSTANCE_CONNECTION: Connection? = null

        @Volatile
        private var INSTANCE_TRANSMISSION: Transmission? = null

        fun build(type: Int): NetWorkService {
            return when (type) {
                IOT_WLAN_CONNECTION_SERVICE -> {
                    //单例模式，确保只有一个服务类在运行
                    return buildIotConnectionService()
                }

                IOT_WLAN_TRANSMISSION_SERVICE -> {
                    //单例模式，确保只有一个服务类在运行
                    return buildIotTransmissionService()
                }

                else -> buildIotConnectionService()
            }
        }

        fun buildIotConnectionService(): Connection {
            //初始化读取网络状态
            val wifiManager =
                SmartWardrobeApplication.getContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectivityManager =
                SmartWardrobeApplication.getContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (INSTANCE_CONNECTION == null) {
                synchronized(this) {
                    if (INSTANCE_CONNECTION == null) {
                        INSTANCE_CONNECTION = IotConnectionImpl(wifiManager, connectivityManager)
                    }
                }
            }
            // 返回一个实例
            return INSTANCE_CONNECTION!!
        }

        fun buildIotTransmissionService(): Transmission {
            if (INSTANCE_TRANSMISSION == null) {
                synchronized(this) {
                    if (INSTANCE_TRANSMISSION == null) {
                        INSTANCE_TRANSMISSION = IotTransmissionImpl()
                    }
                }
            }
            // 返回一个实例
            return INSTANCE_TRANSMISSION!!
        }


        // 添加方法来构建互联网连接服务
        fun buildInternetConnectionService(): Connection {
            // 这里应该实现互联网连接服务的初始化
            if (INSTANCE_CONNECTION == null) {
                synchronized(this) {
                    if (INSTANCE_CONNECTION == null) {
                        INSTANCE_CONNECTION = InternetConnectionImpl()
                    }
                }
            }
            // 返回一个实例
            return INSTANCE_CONNECTION!!
        }

        // 添加方法来构建互联网传输服务
        fun buildInternetTransmissionService(): Transmission {
            // 这里应该实现互联网传输服务的初始化

            if (INSTANCE_TRANSMISSION == null) {
                synchronized(this) {
                    if (INSTANCE_TRANSMISSION == null) {
                        INSTANCE_TRANSMISSION = InternetTransmissionImpl()
                    }
                }
            }
            // 返回一个实例
            return INSTANCE_TRANSMISSION!!
        }
    }
}