package com.mamh.smartwardrobe.bean.item

/**
 * @description 衣物条目类，用于描述衣物的各种属性，包括颜色、款式、材质、尺寸、是否在衣柜中、悬挂位置、品牌、购买日期、是否干净、上次穿的时间等信息
 * @author Mamh
 * @Date 2024/5/15
 **/
data class ClothItem(
    val id: String,             // 唯一标识符
    val name: String,           // 衣服的名字
    val colors: List<String>,   // 衣物颜色列表
    val style: String,          // 衣物款式
    val material: String,       // 衣物材质，如 wool、cotton、linen 等
    val size: String,           // 衣物尺寸
    var isInCloset: Boolean,    // 是否在衣柜中
    var hangPosition: Int,      // 悬挂位置
    val brand: String,          // 品牌
    val purchaseDate: String,   // 购买日期
    var isClean: Boolean,       // 衣物是否干净
    var lastWornDate: String,   // 上次穿的时间
    var tags: MutableList<String>,  // 衣物标签集合
    var recommendationScore: Double = 0.0  // 推荐指数
)
