package com.smart.utube.SearchData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Search_Table")
class SearchDataModel(
        @PrimaryKey(autoGenerate = true)
        val id : Int,
        val name : String
) {
}