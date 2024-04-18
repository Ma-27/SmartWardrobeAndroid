package com.mamh.smartwardrobe.ui.console

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mamh.smartwardrobe.data.AppRepository

class ConsoleViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository by lazy {
        AppRepository.getInstanceForInternet()
    }
    var repository: AppRepository = _repository

}