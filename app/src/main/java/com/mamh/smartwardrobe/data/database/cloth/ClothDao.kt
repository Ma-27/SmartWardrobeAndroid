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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(clothItem: ClothItemEntity)

    @Update
    suspend fun updateCloth(clothItem: ClothItemEntity): Int

    @Delete
    suspend fun deleteCloth(clothItem: ClothItemEntity): Int
}