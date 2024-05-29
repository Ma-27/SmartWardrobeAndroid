package com.mamh.smartwardrobe.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
@description
@author Mamh
@Date 2024/5/29
 **/

class Utility {
    companion object {
        fun getCurrentDate(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }
    }
}