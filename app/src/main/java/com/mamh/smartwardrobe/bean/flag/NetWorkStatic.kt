package com.mamh.smartwardrobe.bean.flag

//字段类，通过字段常量选择网络模式，选择IOT模式(2)为物联网模式，wifi接入局域网和传感器网络；当选择(1)时接入互联网
class NetWorkStatic {
    companion object {
        const val INTERNET_API_SERVICE = 1

        const val IOT_WLAN_CONNECTION_SERVICE = 2

        const val DYNAMIC_IP_DISTRIBUTION = 3

        const val IOT_WLAN_TRANSMISSION_SERVICE = 4
    }
}