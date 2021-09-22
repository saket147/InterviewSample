package com.example.bigstepsampleapp.database.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.bigstepsampleapp.network.model.Results
import kotlinx.coroutines.flow.Flow

@Dao
interface VideosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(result: Results?)

    @Query("SELECT * FROM table_videos")
    fun getAllSavedVideos(): Flow<List<Results?>?>?

    @Query("DELETE FROM table_videos")
    fun nukeTable()

    @RawQuery
    fun vacuumDb(supportSQLiteQuery: SupportSQLiteQuery?): Int
}