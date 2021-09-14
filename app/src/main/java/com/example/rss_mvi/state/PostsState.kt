package com.example.rss_mvi.state

import android.content.Context
import com.example.rss_mvi.model.RssPost

data class PostsViewState(
    val loadCachedPostsStatus: LoadCachedPostsStatus,
    val cachedPosts: List<RssPost>?,
    val fetchPostsStatus: FetchPostsStatus,
    val fetchedPosts: List<RssPost>?,
)

sealed class PostsViewEffect {
    data class ShowSnackbar(val message: String) : PostsViewEffect()
}

sealed class PostsViewEvent {
    data class LoadCachedPosts(val context: Context, val feedId: Int) : PostsViewEvent()
    data class FetchPosts(val context: Context, var url: String, val feedId: Int) : PostsViewEvent()
}

sealed class LoadCachedPostsStatus {
    object Idle : LoadCachedPostsStatus()
    object Loading : LoadCachedPostsStatus()
    object Loaded : LoadCachedPostsStatus()
}

sealed class FetchPostsStatus {
    object Idle : FetchPostsStatus()
    object Fetching : FetchPostsStatus()
    object Fetched : FetchPostsStatus()
    object NotFetched : FetchPostsStatus()
}