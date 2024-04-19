package com.mamh.smartwardrobe.ui.console

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamh.smartwardrobe.data.AppRepository
import timber.log.Timber

class ConsoleViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: AppRepository by lazy {
        AppRepository.getInstanceForInternet()
    }
    var repository: AppRepository = _repository

    //缓存温度调节的目标温度
    private val _targetTemperature = _repository.targetTemperature
    val targetTemperature: LiveData<Int>
        get() = _targetTemperature

    // 滑动时可见的目标温度，当结束滑动时，设置为目标温度
    private val _pendingTemperature = MutableLiveData<Int>().apply {
        value = 15
    }
    val pendingTemperature: LiveData<Int>
        get() = _pendingTemperature


    //缓存温度调节的当前温度
    private val _currentTemperature = _repository.currentTemperature
    val currentTemperature: LiveData<Float>
        get() = _currentTemperature


    // 温度控制是否自动
    private val _temperatureControlAuto = _repository.temperatureControlAuto
    val temperatureControlAuto: LiveData<Boolean>
        get() = _temperatureControlAuto


    // 更改温度控制是否自动模式
    fun setTemperatureControlAuto(value: Boolean) {
        _repository.setTemperatureControlAuto(value)
    }

    // 设置目标温度
    fun setTargetTemperature() {
        Timber.d("设置目标温度为${pendingTemperature.value}")
        pendingTemperature.value?.let { _repository.setTargetTemperature(it) }
    }

    // 设置目标温度
    fun setPendingTemperature(temperature: Int) {
        _pendingTemperature.value = temperature
    }

}