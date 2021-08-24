package com.smart.utube.HomeDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.smart.utube.Data.Recent

@Dao
interface HomeDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  addVideo(home : HomeDataset)

    @Query("SELECT * FROM Home_Table ORDER BY id ASC")
    fun readAllData() : LiveData<List<HomeDataset>>

    @Update
    suspend fun updateVideo(home: HomeDataset)

    @Delete
    suspend fun deleteVideo(home: HomeDataset)

    @Query("DELETE FROM Home_Table")
    fun deleteAllList()

}