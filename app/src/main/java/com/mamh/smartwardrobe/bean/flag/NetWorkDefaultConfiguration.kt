package com.mamh.smartwardrobe.bean.flag

class NetWorkDefaultConfiguration {
    companion object {
        const val DEFAULT_PORT_NUMBER = 8080

        //XXX 注意默认ip是String，使用的时候需要转换
        const val DEFAULT_IP_ADDRESS = "192.168.4.1"

        const val DEFAULT_MAC_ADDRESS = "ff:ff:ff:00:00:00"
    }
}