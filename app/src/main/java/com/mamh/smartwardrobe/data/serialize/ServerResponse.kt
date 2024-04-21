package com.mamh.smartwardrobe.data.serialize


/**
@description
@author Mamh
@Date 2024/4/21
 **/

sealed class ServerResponse {
    // 成功响应，包含命令的UUID
    data class Success(val cmdUuid: String) : ServerResponse()

    // 错误响应，包含错误码和错误信息
    data class Error(val errorCode: Int, val errorMessage: String) : ServerResponse()

    // 数据未找到响应
    object DataNotFound : ServerResponse()
}
