package com.smart.utube.SearchData

import androidx.lifecycle.LiveData
import androidx.room.*
import com.smart.utube.Data.Recent

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  addSearch(search: SearchDataModel)

    @Query("SELECT * FROM Search_Table ORDER BY id ASC")
    fun readAllData() : LiveData<List<SearchDataModel>>

    @Delete
    suspend fun deleteSearch(search : SearchDataModel)


}