package com.smart.utube.playlistData

import androidx.lifecycle.LiveData

class PlayListRepository(val playListDAO : PlayListDAO) {

    val readAllData : LiveData<List<PlayList>> = playListDAO.readAllData()

    suspend fun addPlayList (playlist : PlayList) {
        playListDAO.addPlayList(playlist)
    }

    suspend fun updatePlayList(playlist: PlayList){
        playListDAO.updatePlayList(playlist)
    }

    suspend fun deletePlaylist(playlist: PlayList) {
        playListDAO.deletePlayList(playlist)
    }

    suspend fun deleteAllData () {
        playListDAO.deleteAllPlayList()
    }

}