package com.mamh.smartwardrobe.ui.cloth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClothViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClothViewModel::class.java)) {
            return ClothViewModel(application) as T
        } else {
            throw IllegalArgumentException("未知的view model")
        }
    }
}