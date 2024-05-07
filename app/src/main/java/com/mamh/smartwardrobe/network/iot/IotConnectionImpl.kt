package com.mamh.smartwardrobe.network.iot

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.mamh.smartwardrobe.bean.item.WifiItem

class IotConnectionImpl(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
) : IotConnection {
    override fun checkNetworkState() {
        TODO("Not yet implemented")
    }

    override fun connect(item: WifiItem) {
        TODO("Not yet implemented")
    }

    override fun connect() {
        /// DO NOT NEED TO BE IMPLEMENTED
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }


}