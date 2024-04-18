package com.mamh.smartwardrobe.bean.item

data class DataItem(
    //时间戳，用来记录发送和接收的时间
    val timestamp: Long,
    //事件记录，便于记录整个该数据用来做了什么
    var event: String,
    //数据，是发送或者收到的元数据
    val data: String,
    //消息类型，是发送的消息还是接收的消息
    var messageType: Int,
    //消息状态，是否发送或者接收成功
    var messageStatus: Int
)
