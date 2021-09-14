package com.example.rss_mvi.state

import android.content.Context
import com.example.rss_mvi.model.RssFeed

data class AddFeedViewState(
    val saveFeedStatus: SaveFeedStatus
)

sealed class AddFeedViewEffect {
    data class ShowSnackBar(val message: String) : AddFeedViewEffect()
}

sealed class AddFeedViewEvent {
    data class SaveFeed(val context: Context, val feed: RssFeed) : AddFeedViewEvent()
}

sealed class SaveFeedStatus {
    object Idle : SaveFeedStatus()
    object Saving : SaveFeedStatus()
    object Saved : SaveFeedStatus()
}