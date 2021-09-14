package com.example.rss_mvi.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.rss_mvi.model.RssFeed

@Dao
interface RssFeedDao {
    @Query("select * from RssFeed")
    suspend fun getAll(): List<RssFeed>

    @Insert
    suspend fun insert(feed: RssFeed)

    @Delete
    suspend fun delete(feed: RssFeed)
}