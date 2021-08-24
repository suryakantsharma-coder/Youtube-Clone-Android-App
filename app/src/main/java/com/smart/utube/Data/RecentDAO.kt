package com.smart.utube.Data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.smart.utube.RelatedVideoDataset.Item

@Dao
interface RecentDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  addRecent(recent : Recent)

    @Query("SELECT * FROM recent_table ORDER BY id DESC")
    fun readAllData() : LiveData<List<Recent>>

    @Update
    suspend fun updateRecent(recent: Recent)

    @Delete
    suspend fun deleteRecent(recent: Recent)

    @Query("DELETE FROM recent_table")
    fun deleteAllUser()


}