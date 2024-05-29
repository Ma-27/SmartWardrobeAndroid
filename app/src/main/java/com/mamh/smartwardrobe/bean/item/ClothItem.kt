package com.mamh.smartwardrobe.bean.item

/**
 * @description 衣物条目类，用于描述衣物的各种属性，包括颜色、款式、材质、尺寸、是否在衣柜中、悬挂位置、品牌、购买日期、是否干净、上次穿的时间等信息
 * @author Mamh
 * @Date 2024/5/15
 **/
data class ClothItem(
    val id: String,             // 唯一标识符
    var category: String,       // 类别，比如上衣，裤子等等，用作分类
    var name: String,           // 衣服的名字
    var colors: List<String>,   // 衣物颜色列表
    var style: String,          // 衣物款式
    var material: String,       // 衣物材质，如 wool、cotton、linen 等
    var size: String,           // 衣物尺寸
    var isInCloset: Boolean,    // 是否在衣柜中
    var hangPosition: Int,      // 悬挂位置
    val brand: String,          // 品牌
    val purchaseDate: String,   // 购买日期
    var isClean: Boolean,       // 衣物是否干净
    var lastWornDate: String,   // 上次穿的时间
    var tags: MutableList<String>,  // 衣物标签集合
    var recommendationScore: Double = 0.0  // 推荐指数
)
