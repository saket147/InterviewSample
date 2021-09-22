package com.example.bigstepsampleapp.network.repository

import com.example.bigstepsampleapp.network.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getVideos(term: String?, media: String?) = apiHelper.getVideos(term, media)
}