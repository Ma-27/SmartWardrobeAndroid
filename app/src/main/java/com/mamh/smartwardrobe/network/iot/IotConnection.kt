package com.mamh.smartwardrobe.network.iot

import com.mamh.smartwardrobe.bean.item.WifiItem
import com.mamh.smartwardrobe.network.Connection

//网络请求中连接wifi的接口
interface IotConnection : Connection {
    //检查wifi状态
    override fun checkNetworkState()

    //根据指定的item信息，连接到的wifi。注意这里用了函数重载
    override fun connect(item: WifiItem)

    //断开现在的wifi连接
    override fun disconnect()
}
