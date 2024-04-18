package com.mamh.smartwardrobe.network.internet

import com.mamh.smartwardrobe.network.Transmission


/**
@description
@author Mamh
@Date 2024/4/19
 **/

interface InternetTransmission : Transmission {
    override suspend fun onReceiveData()

    override suspend fun onSendData(): Int
}