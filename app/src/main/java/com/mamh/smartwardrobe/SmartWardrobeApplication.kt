package com.mamh.smartwardrobe

import android.app.Application
import android.content.Context
import timber.log.Timber


/**
@description
@author Mamh
@Date 2024/4/17
 **/

class SmartWardrobeApplication : Application() {
    //全局获取context
    companion object {
        lateinit var context: Context
            private set // 限制外部直接修改 context 变量

        @JvmName("getContext1")
        fun getContext(): Context {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        //如果是debug版，才添加timber
        if (BuildConfig.DEBUG) {
            //引入插件Timber，不用每次都去手动打印log
            Timber.plant(object : Timber.DebugTree() {
                //以后只需要搜索 成功 tag即可查看自定义log
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, "$tag 成功 ", message, t)
                }
            })
        }
        context = applicationContext
    }
}