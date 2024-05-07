package com.mamh.smartwardrobe.util

import android.net.wifi.ScanResult
import com.mamh.smartwardrobe.bean.flag.NetWorkDefaultConfiguration
import com.mamh.smartwardrobe.bean.item.WifiItem

//data class转换类，负责将一个data class转换为另一个，或者提取数据并映射
class DataTransferObject {
    companion object {
        //使用map映射将每个对象转化为另一个对象
        //FIXME ，这里的设备mac地址写死了，实际情况下待wifi进行分配
        fun asWifiItem(scanResult: List<ScanResult>): List<WifiItem> {
            return scanResult.map {
                WifiItem(
                    wifiName = it.SSID,
                    apMacAddress = it.BSSID,
                    signalStrength = it.capabilities,
                    frequency = it.frequency,
                    isConnected = "未连接",
                    password = "",
                    deviceMacAddress = NetWorkDefaultConfiguration.DEFAULT_MAC_ADDRESS,
                    deviceIpAddress = NetWorkDefaultConfiguration.DEFAULT_IP_ADDRESS,
                    apIpAddress = "",
                    portNumber = NetWorkDefaultConfiguration.DEFAULT_PORT_NUMBER
                )
            }
        }
    }
}