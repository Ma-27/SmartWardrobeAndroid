package com.mamh.smartwardrobe.network

import com.mamh.smartwardrobe.bean.item.WifiItem


/**
@description
@author Mamh
@Date 2024/4/19
 **/

interface Connection : NetWorkService {
    //检查wifi状态
    fun checkNetworkState()

    // 连接到互联网。注意这里使用了函数重载。这个connect方法单纯是为了在互联网模式下使用的。
    fun connect()

    //根据指定的item信息，连接到的wifi。注意这里用了函数重载。这个connect方法单纯是为了在IOT模式下使用的。
    fun connect(item: WifiItem)

    //断开现在的网络连接，不论是互联网还是Wi-Fi连接
    fun disconnect()
}