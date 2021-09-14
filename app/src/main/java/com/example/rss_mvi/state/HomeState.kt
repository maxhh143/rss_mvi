package com.example.rss_mvi.state

import android.content.Context
import com.example.rss_mvi.model.RssFeed

data class HomeViewState(
    val savedFeeds: MutableList<RssFeed>?,
    val loadSavedFeedsStatus: LoadSavedFeedsStatus,
)

sealed class HomeViewEffect {
    data class ShowSnackBar(val message: String) : HomeViewEffect()
}

sealed class HomeViewEvent {
    data class SaveInitialFeeds(val context: Context) : HomeViewEvent()
    data class LoadSavedFeeds(val context: Context) : HomeViewEvent()
    data class DeleteSavedFeed(val context: Context, val feed: RssFeed) : HomeViewEvent()
}

sealed class LoadSavedFeedsStatus {
    object Loading : LoadSavedFeedsStatus()
    object Reloading : LoadSavedFeedsStatus()
    object Loaded : LoadSavedFeedsStatus()
}