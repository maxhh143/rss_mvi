package com.example.rss_mvi.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.rss_mvi.model.RssPost

@Dao
interface RssPostDao {
    @Query("select * from RssPost where feed_id = :feedId")
    suspend fun getAllById(feedId: Int): List<RssPost>

    @Query("select exists (select * from RssPost where feed_id = :feedId and title = :title and description = :description)")
    suspend fun checkIfRecordExists(feedId: Int, title: String, description: String): Boolean

    @Insert
    suspend fun insert(post: RssPost)

    @Delete
    suspend fun delete(post: RssPost)
}