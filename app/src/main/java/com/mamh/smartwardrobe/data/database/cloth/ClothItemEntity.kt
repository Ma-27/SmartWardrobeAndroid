package com.mamh.smartwardrobe.data.database.cloth

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mamh.smartwardrobe.data.database.user.UserEntity


/**
@description
@author Mamh
@Date 2024/5/30
 **/


@Entity(
    tableName = "clothes",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
data class ClothItemEntity(
    @PrimaryKey(autoGenerate = false)
    val clothId: Int, // 手动分配ID

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "colors")
    val colors: List<String>, // 使用TypeConverter

    @ColumnInfo(name = "style")
    val style: String,

    @ColumnInfo(name = "material")
    val material: String,

    @ColumnInfo(name = "size")
    val size: String,

    @ColumnInfo(name = "isInCloset")
    val isInCloset: Boolean,

    @ColumnInfo(name = "hangPosition")
    val hangPosition: Int,

    @ColumnInfo(name = "brand")
    val brand: String,

    @ColumnInfo(name = "purchaseDate")
    val purchaseDate: String,

    @ColumnInfo(name = "isClean")
    val isClean: Boolean,

    @ColumnInfo(name = "lastWornDate")
    val lastWornDate: String,

    @ColumnInfo(name = "tags")
    val tags: List<String>, // 使用TypeConverter

    @ColumnInfo(name = "recommendationScore")
    val recommendationScore: Double
)

