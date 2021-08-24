package com.smart.utube.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.smart.utube.Network.Repositry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecentViewModel( application : Application) : AndroidViewModel(application) {
    val readAllData : LiveData<List<Recent>>
    private val repository : RecentRepository

    init {
        val recentDAO  = RecentDatabase.getDatabase(application).recentDao()
        repository = RecentRepository(recentDAO)
        readAllData = repository.readAllData
    }


    fun addRecent (recent : Recent){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecent(recent)
        }
    }

    fun deleteAll () {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteALLData()
        }
    }

    fun  deleteRecent (recent: Recent) {
        viewModelScope.launch(Dispatchers.IO ) {
            repository.deleteRecent(recent)
        }
    }

    fun updateRecent (recent: Recent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatRecent(recent)
        }
    }

}