package com.mamh.smartwardrobe.data.database.cloth

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


/**
@description
@author Mamh
@Date 2024/5/30
 **/

@Dao
interface ClothDao {
    @Query("SELECT * FROM clothes WHERE userId = :userId ORDER BY recommendationScore DESC")
    fun getClothesByUserId(userId: Int): LiveData<List<ClothItemEntity>>

    @Query("SELECT * FROM clothes WHERE clothId = :clothId LIMIT 1")
    suspend fun getClothById(clothId: Int): ClothItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(clothItem: ClothItemEntity)

    @Update
    suspend fun updateCloth(clothItem: ClothItemEntity): Int

    @Delete
    suspend fun deleteCloth(clothItem: ClothItemEntity): Int

    @Query("SELECT * FROM clothes WHERE userId = :userId ORDER BY recommendationScore DESC")
    suspend fun getClothesByUserIdSync(userId: Int): List<ClothItemEntity>

    @Query("SELECT * FROM clothes ORDER BY recommendationScore DESC")
    fun getAllClothes(): LiveData<List<ClothItemEntity>>
}