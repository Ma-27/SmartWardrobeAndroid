package com.mamh.smartwardrobe.ui.datacenter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mamh.smartwardrobe.data.AppRepository

class DataCenterViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository =
        AppRepository.Builder()
            .setInternetMode()
            .build()
    var repository: AppRepository = _repository
}