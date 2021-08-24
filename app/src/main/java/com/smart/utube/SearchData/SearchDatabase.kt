package com.smart.utube.SearchData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smart.utube.Data.Recent
import com.smart.utube.Data.RecentDAO
import com.smart.utube.Data.RecentDatabase

@Database(entities = [SearchDataModel::class], version = 1, exportSchema = false)
abstract class SearchDatabase : RoomDatabase() {

    abstract fun searchDao() : SearchDao

    companion object{
        private var INSTANCE : SearchDatabase? = null

        fun getDatabase(context : Context) : SearchDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SearchDatabase::class.java,
                        "Search_Database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}