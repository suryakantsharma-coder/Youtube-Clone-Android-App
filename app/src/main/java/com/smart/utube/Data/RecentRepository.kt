package com.smart.utube.Data

import androidx.lifecycle.LiveData

class RecentRepository(private val recentDAO : RecentDAO) {

    val readAllData : LiveData<List<Recent>> = recentDAO.readAllData()

    suspend fun addRecent (recent : Recent){
        recentDAO.addRecent(recent)
    }

    suspend fun deleteALLData () {
        recentDAO.deleteAllUser()
    }

    suspend fun updatRecent(recent: Recent) {
        recentDAO.updateRecent(recent)
    }

    suspend fun deleteRecent (recent: Recent) {
        recentDAO.deleteRecent(recent)
    }
}