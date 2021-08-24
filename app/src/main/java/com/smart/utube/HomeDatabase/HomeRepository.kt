package com.smart.utube.HomeDatabase

import androidx.lifecycle.LiveData
import com.smart.utube.Data.Recent

class HomeRepository (val homeDao : HomeDAO) {

    val readAllData : LiveData<List<HomeDataset>> = homeDao.readAllData()

    suspend fun addVideo (home : HomeDataset){
        homeDao.addVideo(home)
    }

    suspend fun deleteAllData () {
        homeDao.deleteAllList()
    }

    suspend fun updatVideo(home: HomeDataset) {
        homeDao.updateVideo(home)
    }

    suspend fun deleteVideo (home: HomeDataset) {
        homeDao.deleteVideo(home)
    }
}