package com.mamh.smartwardrobe

import com.mamh.smartwardrobe.bean.item.ClothItem
import com.mamh.smartwardrobe.bean.netpacket.UsefulDailyWeatherDetail
import com.mamh.smartwardrobe.ui.cloth.ClothModel
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.util.logging.Logger

class ClothModelTest {

    private val logger = Logger.getLogger(ClothModelTest::class.java.name)

    // 测试推荐函数
    @Test
    fun testRecommend() {
        // 初始化天气信息
        val weatherDetail = UsefulDailyWeatherDetail(
            location = "Lat: 34.05, Lon: -118.25",
            temperature = 20,
            humidity = 50,
            pm25 = 30,
            dressingIndex = 4,
            dressingAdvice = "适合穿牛仔裤和风衣",
            weatherCondition = "晴朗"
        )

        // 初始化衣物列表
        val clothes = listOf(
            ClothItem(
                id = "1",
                name = "白色的棉质T恤",
                colors = listOf("白色"),
                style = "T恤",
                material = "cotton",
                size = "M",
                isInCloset = true,
                hangPosition = 1,
                brand = "品牌A",
                purchaseDate = "2023-04-01",
                isClean = true,
                lastWornDate = "2023-04-08",
                tags = mutableListOf("休闲", "夏季")
            ),
            ClothItem(
                id = "2",
                name = "米色的休闲短裤",
                colors = listOf("米色"),
                style = "短裤",
                material = "linen",
                size = "M",
                isInCloset = true,
                hangPosition = 2,
                brand = "品牌B",
                purchaseDate = "2023-04-01",
                isClean = true,
                lastWornDate = "2023-04-29",
                tags = mutableListOf("休闲", "夏季")
            ),
            ClothItem(
                id = "3",
                name = "黑色的羊毛外套",
                colors = listOf("黑色"),
                style = "外套",
                material = "wool",
                size = "L",
                isInCloset = true,
                hangPosition = 3,
                brand = "品牌C",
                purchaseDate = "2022-12-01",
                isClean = true,
                lastWornDate = "2023-01-15",
                tags = mutableListOf("冬季", "正式")
            ),
            ClothItem(
                id = "4",
                name = "蓝色的牛仔裤",
                colors = listOf("蓝色"),
                style = "长裤",
                material = "denim",
                size = "M",
                isInCloset = true,
                hangPosition = 4,
                brand = "品牌D",
                purchaseDate = "2023-03-15",
                isClean = true,
                lastWornDate = "2023-05-01",
                tags = mutableListOf("休闲", "秋季")
            ),
            ClothItem(
                id = "5",
                name = "红色的丝绸连衣裙",
                colors = listOf("红色"),
                style = "连衣裙",
                material = "silk",
                size = "S",
                isInCloset = true,
                hangPosition = 5,
                brand = "品牌E",
                purchaseDate = "2023-02-20",
                isClean = true,
                lastWornDate = "2023-04-25",
                tags = mutableListOf("夏季", "正式")
            ),
            ClothItem(
                id = "6",
                name = "灰色的羊毛衫",
                colors = listOf("灰色"),
                style = "毛衫",
                material = "wool",
                size = "M",
                isInCloset = true,
                hangPosition = 6,
                brand = "品牌F",
                purchaseDate = "2022-11-05",
                isClean = true,
                lastWornDate = "2023-02-10",
                tags = mutableListOf("冬季", "休闲")
            )
        )

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

        // 测试推荐结果的评分范围是否在1-4之间
        assertTrue(recommendedClothes.all { it.recommendationScore in 0.0..5.0 })
    }
}
