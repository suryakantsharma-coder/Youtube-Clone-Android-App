package com.smart.utube.playlistData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smart.utube.Data.Recent
import com.smart.utube.Data.RecentDatabase
import com.smart.utube.Data.RecentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PLayListViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData : LiveData<List<PlayList>>
    private val repository : PlayListRepository

    init {
        val playListDAO = PlayListDatabase.getDatabase(application).playlistDAO()
        repository = PlayListRepository(playListDAO)
        readAllData = repository.readAllData
    }


    fun addPlaylist (playList: PlayList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPlayList(playList)
        }
    }

    fun deleteAll () {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun  deletePlayList (playList : PlayList) {
        viewModelScope.launch(Dispatchers.IO ) {
            repository.deletePlaylist(playList)
        }
    }

    fun updatePlayList (playList: PlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlayList(playList)
        }
    }


}