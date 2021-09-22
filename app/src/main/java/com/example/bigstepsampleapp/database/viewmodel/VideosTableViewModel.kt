package com.example.bigstepsampleapp.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.bigstepsampleapp.database.repository.VideosTableRepository
import com.example.bigstepsampleapp.network.model.Results
import kotlinx.coroutines.flow.Flow

class VideosTableViewModel(application: Application) : AndroidViewModel(application) {

    private val videosTableRepository: VideosTableRepository = VideosTableRepository(application)
    internal val savedVideos: Flow<List<Results?>?>? = videosTableRepository.getAllSavedVideos()

    suspend fun insertItem(result: Results?) {
        videosTableRepository.insertItem(result)
    }

    suspend fun nukeTable(supportSQLiteQuery: SupportSQLiteQuery?) {
        videosTableRepository.nukeTable(supportSQLiteQuery)
    }
}