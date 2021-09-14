package com.example.rss_mvi.api

import com.example.rss_mvi.model.Rss2Json
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    companion object {
        fun getInstance() : ServerApi = Retrofit.Builder()
            .baseUrl("https://api.rss2json.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServerApi::class.java)
    }

    @GET("v1/api.json")
    suspend fun getFeed(@Query("rss_url") rssUrl: String): Rss2Json
}