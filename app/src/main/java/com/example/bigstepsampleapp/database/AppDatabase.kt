package com.example.bigstepsampleapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bigstepsampleapp.database.dao.VideosDao
import com.example.bigstepsampleapp.network.model.Results
import com.example.bigstepsampleapp.utils.DBConstants

@Database(entities = [Results::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun videosDao(): VideosDao?
    companion object{
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DBConstants.TAG_APP_DB_NAME
            ).build()
    }
}