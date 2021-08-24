package com.smart.utube.SearchData

import androidx.lifecycle.LiveData
import com.smart.utube.Data.Recent
import com.smart.utube.Data.RecentDAO

class SearchRepository(private val searchDAO : SearchDao) {

    val readAllData : LiveData<List<SearchDataModel>> = searchDAO.readAllData()

    suspend fun addRecent (search : SearchDataModel){
        searchDAO.addSearch(search)
    }

    suspend fun deleteSearch (search: SearchDataModel) {
       searchDAO.deleteSearch(search)
    }
}