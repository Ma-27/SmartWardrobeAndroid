package com.mamh.smartwardrobe.network.internet

import com.mamh.smartwardrobe.bean.netpacket.DailyWeatherResponse
import com.mamh.smartwardrobe.bean.netpacket.LifeIndexResponse
import com.mamh.smartwardrobe.bean.netpacket.RealtimeWeatherResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// 彩云天气API的基础URL
private const val BASE_URL = "https://api.caiyunapp.com/v2.6/"

// 使用Moshi作为JSON解析库
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// 生成一个Retrofit请求实例
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// 定义接口用于彩云天气API的调用
interface CaiyunWeatherApiService {
    // 获取特定地点的每日天气数据
    @GET("{token}/{location}/daily")
    suspend fun getDailyWeather(
        @Path("token") token: String,
        @Path("location") location: String,
        @Query("dailysteps") dailysteps: String = "1",
        @Query("alert") alert: Boolean = true
    ): DailyWeatherResponse? // 每日天气的返回类型


    @Deprecated("This is not useful")
    // 获取特定地点的实时天气数据
    @GET("{token}/{location}/realtime")
    suspend fun getRealtimeWeather(
        @Path("token") token: String,
        @Path("location") location: String,
        @Query("alert") alert: Boolean = true
    ): RealtimeWeatherResponse? // 实况天气的返回类型


    @Deprecated("This API v3 cannot be used")
    // 生活指数API的接口
    @GET("v3/lifeindex")
    suspend fun getLifeIndex(
        @Query("token") token: String,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("days") days: Int = 1,
        @Query("fields") fields: String = "1,2,3"
    ): LifeIndexResponse? // 生活指数的返回类型

}

// 生成接口实现类的单例
object CaiyunWeatherApi {
    val retrofitService: CaiyunWeatherApiService by lazy {
        retrofit.create(CaiyunWeatherApiService::class.java)
    }
}