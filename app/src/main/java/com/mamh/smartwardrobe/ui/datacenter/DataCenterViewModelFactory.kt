package com.mamh.smartwardrobe.ui.datacenter

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DataCenterViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataCenterViewModel::class.java)) {
            return DataCenterViewModel(application) as T
        } else {
            throw IllegalArgumentException("未知的view model")
        }
    }
}