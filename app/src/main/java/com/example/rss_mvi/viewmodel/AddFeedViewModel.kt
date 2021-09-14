package com.example.rss_mvi.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.rss_mvi.internal.ViewModel
import com.example.rss_mvi.repository.FeedRepository
import com.example.rss_mvi.state.AddFeedViewEffect
import com.example.rss_mvi.state.AddFeedViewEvent
import com.example.rss_mvi.state.AddFeedViewState
import com.example.rss_mvi.state.SaveFeedStatus
import kotlinx.coroutines.launch

class AddFeedViewModel(app: Application) : ViewModel<AddFeedViewState, AddFeedViewEffect, AddFeedViewEvent>(app) {
    private val feedRepository: FeedRepository = FeedRepository.getInstance()

    init { viewState = AddFeedViewState(saveFeedStatus = SaveFeedStatus.Idle) }

    override fun process(viewEvent: AddFeedViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is AddFeedViewEvent.SaveFeed -> {
                viewState = viewState.copy(saveFeedStatus = SaveFeedStatus.Saving)
                viewModelScope.launch {
                    feedRepository.saveFeed(viewEvent.context, viewEvent.feed).also {
                        viewState = viewState.copy(saveFeedStatus = SaveFeedStatus.Saved)
                    }
                }
            }
        }
    }
}