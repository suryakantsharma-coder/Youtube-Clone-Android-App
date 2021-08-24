package com.smart.utube.playlistData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smart.utube.Data.RecentDAO
import com.smart.utube.Data.RecentDatabase
import com.smart.utube.Fragments.playlistFragment

@Database(entities = [PlayList::class], version = 1, exportSchema = false)
abstract class PlayListDatabase : RoomDatabase() {

    abstract fun playlistDAO() : PlayListDAO

    companion object{
        private var INSTANCE : PlayListDatabase? = null

        fun getDatabase(context : Context) : PlayListDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlayListDatabase::class.java,
                        "PlayList_Database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}