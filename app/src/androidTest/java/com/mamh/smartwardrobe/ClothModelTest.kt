package com.mamh.smartwardrobe

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.ui.cloth.ClothModel
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.logging.Logger

@RunWith(AndroidJUnit4::class)
class ClothModelTest {

    private val logger = Logger.getLogger(ClothModelTest::class.java.name)

    // 测试推荐函数
    @Test
    fun testRecommend() {
        // 获取上下文
        val context: Context = ApplicationProvider.getApplicationContext()

        // 初始化天气信息
        val weatherDetail = UsefulDailyWeatherDetail(
            location = "重庆市 渝北区",         // 地理位置，格式为"Lat: 纬度, Lon: 经度"
            temperature = 20,         // 温度信息，平均温度
            humidity = 50,            // 湿度信息，平均湿度
            pm25 = 78,                // PM2.5信息
            dressingIndex = 2,       // 穿衣指数，包含指数和描述,2代表比较炎热
            dressingAdvice = "建议穿清爽衬衫，轻薄裤，舒适帆布鞋",   // 穿衣建议
            weatherCondition = "晴朗",  // 天气状况，如“晴朗”或“阴天”
            comfortIndex = 6,       // 舒适度指数
            comfortDescription = "未知的舒适度指数",  // 舒适度描述
        )

        // 初始化衣物列表
        val clothes = listOf(
            ClothItem(
                id = "1",
                name = "白色的棉质T恤",
                colors = mutableListOf("#FFFFFF", "#FE3CA2"),
                style = "轻便透气款",
                material = "纯棉材质",
                size = "M",
                isInCloset = true,
                hangPosition = 1,
                brand = "品牌A",
                purchaseDate = "2023年4月1日",
                isClean = true,
                lastWornDate = "2024年1月1日",
                tags = mutableListOf("青春活力", "清爽舒适", "适合温差较大时穿"),
                category = "衬衫",
                recommendationScore = 0.0
            ),
            // 高评分衣物
            ClothItem(
                id = "2",
                name = "红色的丝绸连衣裙",
                colors = mutableListOf("#FF0000", "#FE3CA2"),  // 红色对应的颜色代码
                style = "连衣裙",
                material = "丝绸",
                size = "S",
                isInCloset = true,
                hangPosition = 5,
                brand = "品牌E",
                purchaseDate = "2023年2月20日",
                isClean = true,
                lastWornDate = "2024年4月25日",
                tags = mutableListOf("轻便", "长袖"),
                category = "裤子",
                recommendationScore = 0.0
            ),
            ClothItem(
                id = "3",
                name = "黑色的羊毛外套",
                colors = mutableListOf("#000000", "#FE3CA2"),  // 黑色对应的颜色代码
                style = "外套",
                material = "羊毛",
                size = "L",
                isInCloset = true,
                hangPosition = 3,
                brand = "品牌C",
                purchaseDate = "2022年12月1日",
                isClean = true,
                lastWornDate = "2024年1月15日",
                tags = mutableListOf("冬季", "正式","长袖"),
                category = "衬衫",
                recommendationScore = 0.0
            ),
            ClothItem(
                id = "4",
                name = "蓝色的牛仔裤",
                colors = mutableListOf("#0000FF", "#FE3CA2"),  // 蓝色对应的颜色代码
                style = "长裤",
                material = "棉",
                size = "M",
                isInCloset = true,
                hangPosition = 4,
                brand = "品牌D",
                purchaseDate = "2023年3月15日",
                isClean = true,
                lastWornDate = "2023年5月1日",
                tags = mutableListOf("休闲", "秋季"),
                category = "裤子",
                recommendationScore = 0.0
            ),
            ClothItem(
                id = "5",
                name = "红色的丝绸连衣裙",
                colors = mutableListOf("#FF0000", "#FE3CA2"),  // 红色对应的颜色代码
                style = "连衣裙",
                material = "丝绸",
                size = "S",
                isInCloset = true,
                hangPosition = 5,
                brand = "品牌E",
                purchaseDate = "2023年2月20日",
                isClean = true,
                lastWornDate = "2023年4月25日",
                tags = mutableListOf("夏季", "正式"),
                category = "衬衫",
                recommendationScore = 0.0
            ),
            ClothItem(
                id = "6",
                name = "灰色的羊毛衫",
                colors = mutableListOf("#808080", "#FE3CA2"),  // 灰色对应的颜色代码
                style = "毛衫",
                material = "涤纶",
                size = "M",
                isInCloset = true,
                hangPosition = 6,
                brand = "品牌F",
                purchaseDate = "2022年11月5日",
                isClean = true,
                lastWornDate = "2023年2月10日",
                tags = mutableListOf("冬季", "休闲"),
                category = "衬衫",
                recommendationScore = 0.0
            ),
            // 什么都不知道的要中偏低评分
            ClothItem(
                id = "7",
                name = "不知道",
                colors = mutableListOf("#808080", "#FE3CA2"),  // 灰色对应的颜色代码
                style = "不知道",
                material = "不知道",
                size = "不知道",
                isInCloset = true,
                hangPosition = 6,
                brand = "品牌G",
                purchaseDate = "2022年11月5日",
                isClean = true,
                lastWornDate = "2019年9月5日",
                tags = mutableListOf("不知道1", "不知道2"),
                category = "衬衫",
                recommendationScore = 0.0
            ),
            ClothItem(
                id = "8",
                name = "灰色的羊毛衫",
                colors = mutableListOf("#808080", "#FE3CA2"),  // 灰色对应的颜色代码
                style = "毛衫",
                material = "亚麻",
                size = "M",
                isInCloset = true,
                hangPosition = 6,
                brand = "品牌F",
                purchaseDate = "2022年11月5日",
                isClean = true,
                lastWornDate = "2023年2月10日",
                tags = mutableListOf("冬季", "休闲"),
                category = "衬衫",
                recommendationScore = 0.0
            ),
        )

        // 确保在主线程上设置LiveData的值
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            ClothModel.repository.setUsefulDailyWeatherDetail(weatherDetail)

        }

        // 调用 recommend 函数
        val recommendedClothes = ClothModel.recommend(weatherDetail, clothes)

        // 打印推荐结果
        logger.info("推荐结果:")
        recommendedClothes.forEach { cloth ->
            logger.info(
                """
                衣物: ${cloth.name}
                颜色: ${cloth.colors.joinToString()}
                款式: ${cloth.style}
                材质: ${cloth.material}
                尺寸: ${cloth.size}
                品牌: ${cloth.brand}
                购买日期: ${cloth.purchaseDate}
                上次穿的时间: ${cloth.lastWornDate}
                标签: ${cloth.tags.joinToString()}
                推荐指数: ${cloth.recommendationScore}
            """.trimIndent()
            )
        }

        // 测试推荐结果的评分范围是否在0-5之间
        assertTrue(recommendedClothes.all { it.recommendationScore in 0.0..100.0 })
    }
}
