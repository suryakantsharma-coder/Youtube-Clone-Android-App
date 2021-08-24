package com.smart.utube.HomeDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.smart.utube.RelatedVideoDataset.Id
import com.smart.utube.RelatedVideoDataset.Snippet
import com.smart.utube.RelatedVideoDataset.Thumbnails

@Entity(tableName = "Home_Table")
class HomeDataset (
        @PrimaryKey(autoGenerate = true)
        val id : Int,
        val title : String,
        val description : String,
        val publishedAt : String,
        val channelName : String,
        val thumbnails : String,
        val videoId : String,
        ){

}
