package com.mamh.smartwardrobe.bean.item


/**
@description
@author Mamh
@Date 2024/5/30
 **/

data class ClothItemForMCU(
    val id: String,
    val color: String,
    val style: String,
    val material: String,
    val size: String,
    val isInCloset: Boolean,
    val hangPosition: Int,
    val brand: String,
    val purchaseDate: String,
    val isClean: Boolean,
    val lastWornDate: String
)