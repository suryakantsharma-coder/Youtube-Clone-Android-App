package com.smart.utube.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Recent::class], version = 1, exportSchema = false)
abstract class RecentDatabase : RoomDatabase() {

    abstract fun recentDao() : RecentDAO

    companion object{
        private var INSTANCE : RecentDatabase? = null

        fun getDatabase(context : Context) : RecentDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecentDatabase::class.java,
                        "Recent_Database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}