package com.mamh.smartwardrobe.network.internet

import com.mamh.smartwardrobe.network.Connection


/**
@description
@author Mamh
@Date 2024/4/19
 **/

interface InternetConnection : Connection {
    //检查互联网连接状态
    override fun checkNetworkState()

    //连接到互联网
    override fun connect()

    //断开现在的wifi连接
    override fun disconnect()
}