package com.example.rss_mvi.repository

import android.content.Context
import com.example.rss_mvi.api.DatabaseApi
import com.example.rss_mvi.api.ServerApi
import com.example.rss_mvi.internal.HttpResponse
import com.example.rss_mvi.model.RssPost
import com.example.rss_mvi.model.Rss2Json

class PostRepository {
    companion object {
        @Volatile
        private var instance: PostRepository? = null
        fun getInstance() = instance ?: synchronized(this) { instance ?: PostRepository().also { instance = it } }
    }

    suspend fun loadCachedPostsById(context: Context, feedId: Int): List<RssPost> =
        DatabaseApi.getInstance(context).rssPostDao().getAllById(feedId)

    suspend fun cachePost(context: Context, post: RssPost) {
        DatabaseApi.getInstance(context).rssPostDao().insert(post)
    }

    suspend fun isPostCached(context: Context, post: RssPost): Boolean =
        DatabaseApi.getInstance(context).rssPostDao().checkIfRecordExists(post.feedId!!, post.title!!, post.description!!)

    suspend fun deleteCachedPosts(context: Context, posts: List<RssPost>) {
        posts.forEach { DatabaseApi.getInstance(context).rssPostDao().delete(it) }
    }

    suspend fun getFeed(url: String): HttpResponse<Rss2Json> {
        val feed: Rss2Json = try {
            ServerApi.getInstance().getFeed(rssUrl = url)
        } catch (ex: Exception) {
            return HttpResponse.Error(ex)
        }

        return HttpResponse.Success(feed)
    }
}