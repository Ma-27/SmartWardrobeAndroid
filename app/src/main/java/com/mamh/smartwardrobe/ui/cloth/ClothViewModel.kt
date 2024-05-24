package com.mamh.smartwardrobe.ui.cloth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.data.AppRepository

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
}