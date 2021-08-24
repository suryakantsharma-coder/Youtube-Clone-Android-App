package com.smart.utube.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Recent_Table")
class Recent(
        @PrimaryKey(autoGenerate = true)
        val id : Int,
        val title : String,
        val thumbnail : String,
        val description : String,
        val publish : String,
        val VideoId : String,
        val time : Float

) {


}