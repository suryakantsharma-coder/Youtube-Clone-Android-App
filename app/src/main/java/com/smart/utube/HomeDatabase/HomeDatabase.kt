package com.smart.utube.HomeDatabase

import android.content.Context
import androidx.room.*

@Database(entities = [HomeDataset::class], version = 2, exportSchema = false)
abstract class HomeDatabase : RoomDatabase(){

    abstract fun HomeDAO() : HomeDAO

    companion object{
        private var INSTANCE : HomeDatabase? = null

        fun getDatabase(context : Context) : HomeDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        HomeDatabase::class.java,
                        "Home_Table"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}