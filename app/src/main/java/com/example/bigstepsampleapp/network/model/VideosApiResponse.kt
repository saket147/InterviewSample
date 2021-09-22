package com.example.bigstepsampleapp.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bigstepsampleapp.utils.DBConstants

data class VideosApiResponse(
    val resultCount: Int?,
    val results: Collection<Results?>?
)

@Entity(tableName = DBConstants.TAG_VIDEOS_TABLE)
data class Results(
    @PrimaryKey
    val trackId: Long?,
    val wrapperType: String?,
    val kind: String?,
    val artistId: Long?,
    val collectionId: Long?,
    val artistName: String?,
    val collectionName: String?,
    val trackName: String?,
    val collectionCensoredName: String?,
    val trackCensoredName: String?,
    val artistViewUrl: String?,
    val collectionViewUrl: String?,
    val trackViewUrl: String?,
    val previewUrl: String?,
    val artworkUrl30: String?,
    val artworkUrl60: String?,
    val artworkUrl100: String?,
    val collectionPrice: Double?,
    val trackPrice: Double?,
    val releaseDate: String?,
    val collectionExplicitness: String?,
    val trackExplicitness: String?,
    val discCount: Int?,
    val discNumber: Int?,
    val trackCount: Int?,
    val trackNumber: Int?,
    val trackTimeMillis: Long?,
    val country: String?,
    val currency: String?,
    val primaryGenreName: String?
)