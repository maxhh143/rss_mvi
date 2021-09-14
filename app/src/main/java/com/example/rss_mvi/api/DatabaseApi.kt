package com.example.rss_mvi.api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rss_mvi.dao.RssFeedDao
import com.example.rss_mvi.dao.RssPostDao
import com.example.rss_mvi.model.RssFeed
import com.example.rss_mvi.model.RssPost

@Database(entities = [RssFeed::class, RssPost::class], version = 7, exportSchema = false)
abstract class DatabaseApi : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: DatabaseApi? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context, DatabaseApi::class.java, "test-database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build().also {
                instance = it
            }
        }
    }

    abstract fun rssFeedDao(): RssFeedDao

    abstract fun rssPostDao(): RssPostDao
}