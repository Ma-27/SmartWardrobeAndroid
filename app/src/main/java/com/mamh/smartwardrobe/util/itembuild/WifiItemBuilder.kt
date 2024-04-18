package com.mamh.smartwardrobe.util.itembuild

import com.mamh.smartwardrobe.bean.flag.NetWorkDefaultConfiguration
import com.mamh.smartwardrobe.bean.item.WifiItem

class WifiItemBuilder {
    companion object {
        fun buildWifiItem(): WifiItem {
            return WifiItem(
                "ERROR",
                "no",
                "1",
                1,
                "no",
                "no",
                NetWorkDefaultConfiguration.DEFAULT_MAC_ADDRESS,
                NetWorkDefaultConfiguration.DEFAULT_IP_ADDRESS,
                "",
                NetWorkDefaultConfiguration.DEFAULT_PORT_NUMBER
            )
        }
    }
}