package com.mamh.smartwardrobe.ui.cloth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mamh.smartwardrobe.bean.flag.MessageType
import com.mamh.smartwardrobe.bean.flag.TransmissionStatus
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.data.AppRepository
import com.mamh.smartwardrobe.data.database.cloth.ClothDao
import com.mamh.smartwardrobe.util.DataTransferObject
import com.mamh.smartwardrobe.util.itembuild.DataItemBuilder
import kotlinx.coroutines.launch

class ClothViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository


    /// ------------------------- 衣物数据部分-----------------------------------------------------------------------------

    // 衣物列表
    private val _clothList = _repository.clothList
    var clothList: LiveData<List<ClothItem>> = _clothList


    /// ------------------------- 衣物数据部分-----------------------------------------------------------------------------

    // 删除指定的 ClothItem
    suspend fun removeClothItem(item: ClothItem, clothDao: ClothDao) {
        repository.removeClothItem(item)
        // 数据库中删除
        viewModelScope.launch {
            val clothItemEntity = DataTransferObject.fromClothItemToEntity(item, 0)
            clothDao.deleteCloth(clothItemEntity)
        }
    }


    // 设置新列表
    fun setClothList(value: List<ClothItem>) {
        repository.setClothList(value)
    }


    /// ---------------------------- 处理逻辑 --------------------------------------------------------
    //向目标主机发送数据
    fun sendData(data: String) {
        //构建数据对象
        val dataItem = DataItemBuilder.buildDataItem(data)
        dataItem.messageType = MessageType.MESSAGE_SEND
        dataItem.messageStatus = TransmissionStatus.UNKNOWN
        dataItem.event = DataItemBuilder.determineEventString(data, MessageType.MESSAGE_SEND)
        //存入发送数据列表中，发送数据
        repository.addDataItemToList(dataItem)
    }
}