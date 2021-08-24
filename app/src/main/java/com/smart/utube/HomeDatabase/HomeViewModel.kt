package com.smart.utube.HomeDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.smart.utube.Data.Recent
import com.smart.utube.Data.RecentDatabase
import com.smart.utube.Data.RecentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application : Application) : AndroidViewModel(application) {
    val readAllData : LiveData<List<HomeDataset>>
    private val repository : HomeRepository

    init {
        val homeDao  = HomeDatabase.getDatabase(application).HomeDAO()
        repository = HomeRepository(homeDao)
        readAllData = repository.readAllData
    }


    fun addVideo (home : HomeDataset){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addVideo(home)
        }
    }

    fun deleteAll () {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun  deleteVideo (home: HomeDataset) {
        viewModelScope.launch(Dispatchers.IO ) {
            repository.deleteVideo(home)
        }
    }

    fun updateVideo (home: HomeDataset) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatVideo(home)
        }
    }

}