package com.smart.utube.playlistData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlayList_Table")
class PlayList (
        @PrimaryKey(autoGenerate = true)
        val id : Int,
        val title : String,
        val thumbnail : String,
        val description : String,
        val publish : String,
        val duration : String,
        val VideoId : String,
        val type : String,
        ) {
}