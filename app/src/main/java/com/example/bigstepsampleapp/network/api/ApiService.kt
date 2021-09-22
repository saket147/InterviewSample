package com.example.bigstepsampleapp.network.api

import com.example.bigstepsampleapp.network.model.VideosApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getVideos(@Query("term") term: String?, @Query("media") media: String?): VideosApiResponse

}