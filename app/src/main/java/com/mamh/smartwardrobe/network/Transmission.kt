package com.mamh.smartwardrobe.network


/**
@description
@author Mamh
@Date 2024/4/19
 **/

interface Transmission : NetWorkService {
    suspend fun onReceiveData()

    suspend fun onSendData(): Int
}