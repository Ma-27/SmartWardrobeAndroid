package com.mamh.smartwardrobe.bean.flag

class TransmissionStatus {
    companion object {
        const val SUCCESS = 0

        const val FAIL = 1

        const val UNKNOWN = 2

        const val ON_SENDING = 3

        const val SOCKET_NULL = 4

        const val UNCONNECTED = 5
    }
}