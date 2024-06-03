package com.mamh.smartwardrobe

import com.mamh.smartwardrobe.ui.cloth.ClothModel.customSigmoid
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomSigmoidTest {

    @Test
    fun testCustomSigmoid() {
        // 定义测试点
        val testPoints = listOf(
            0.0 to 0.6,
            0.5 to 0.710949,
            1.0 to 1.0,
            1.5 to 0.989013,
            2.0 to 0.981684
        )

        // 测试每个点
        testPoints.forEach { (x, expected) ->
            val result = customSigmoid(x)
            println("customSigmoid($x) = $result, expected = $expected")
            assertEquals(expected, result, 0.01)  // 精度范围内的相等检查
        }
    }
}
