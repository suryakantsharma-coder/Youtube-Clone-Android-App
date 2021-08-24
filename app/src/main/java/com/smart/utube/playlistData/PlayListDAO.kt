package com.smart.utube.playlistData

import androidx.lifecycle.LiveData
import androidx.room.*
import com.smart.utube.Data.Recent

@Dao
interface PlayListDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  addPlayList(playList: PlayList)

    @Query("SELECT * FROM playlist_table ORDER BY id DESC")
    fun readAllData() : LiveData<List<PlayList>>

    @Update
    suspend fun updatePlayList(playList: PlayList)

    @Delete
    suspend fun deletePlayList(playList: PlayList)

    @Query("DELETE FROM playlist_table")
    fun deleteAllPlayList()

}