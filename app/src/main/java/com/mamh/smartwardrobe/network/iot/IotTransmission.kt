package com.mamh.smartwardrobe.network.iot

import com.mamh.smartwardrobe.network.Transmission

//数据传输接口，通过这里传输数据
interface IotTransmission : Transmission {
    override suspend fun onReceiveData()

    override suspend fun onSendData(): Int
}
