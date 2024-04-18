package com.mamh.smartwardrobe.ui.console

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ConsoleViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConsoleViewModel::class.java)) {
            return ConsoleViewModel(application) as T
        } else {
            throw IllegalArgumentException("未知的view model")
        }
    }
}