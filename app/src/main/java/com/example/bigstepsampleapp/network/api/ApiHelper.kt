package com.example.bigstepsampleapp.network.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getVideos(term: String?, media: String?) = apiService.getVideos(term, media)
}