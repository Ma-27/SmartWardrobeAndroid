package com.mamh.smartwardrobe.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
@description
@author Mamh
@Date 2024/5/30
 **/

class DataTypeConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Gson().fromJson(it, object : TypeToken<List<String>>() {}.type) }
    }
}