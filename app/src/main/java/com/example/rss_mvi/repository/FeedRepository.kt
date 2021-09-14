package com.example.rss_mvi.repository

import android.content.Context
import com.example.rss_mvi.api.DatabaseApi
import com.example.rss_mvi.model.RssFeed

class FeedRepository {
    companion object {
        @Volatile
        private var instance: FeedRepository? = null
        fun getInstance() = instance ?: synchronized(this) { instance ?: FeedRepository().also { instance = it } }
    }

    suspend fun loadSavedFeeds(context: Context): List<RssFeed> = DatabaseApi.getInstance(context).rssFeedDao().getAll()

    suspend fun saveFeed(context: Context, feed: RssFeed) {
        DatabaseApi.getInstance(context).rssFeedDao().insert(feed)
    }

    suspend fun deleteFeed(context: Context, feed: RssFeed) {
        DatabaseApi.getInstance(context).rssFeedDao().delete(feed)
    }
}