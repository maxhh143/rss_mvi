package com.example.rss_mvi.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.rss_mvi.internal.ViewModel
import com.example.rss_mvi.model.RssFeed
import com.example.rss_mvi.repository.FeedRepository
import com.example.rss_mvi.state.HomeViewEffect
import com.example.rss_mvi.state.HomeViewEvent
import com.example.rss_mvi.state.HomeViewState
import com.example.rss_mvi.state.LoadSavedFeedsStatus
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : ViewModel<HomeViewState, HomeViewEffect, HomeViewEvent>(app) {
    private val feedRepository: FeedRepository = FeedRepository.getInstance()

    init {
        viewState = HomeViewState(
            savedFeeds = null,
            loadSavedFeedsStatus = LoadSavedFeedsStatus.Loading,
        )
    }

    override fun process(viewEvent: HomeViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is HomeViewEvent.LoadSavedFeeds -> {
                viewModelScope.launch {
                    feedRepository.loadSavedFeeds(viewEvent.context).also {
                        viewState = viewState.copy(savedFeeds = it as MutableList<RssFeed>, loadSavedFeedsStatus = LoadSavedFeedsStatus.Loaded)
                    }
                }
            }

            is HomeViewEvent.DeleteSavedFeed -> viewModelScope.launch {
                feedRepository.deleteFeed(viewEvent.context, viewEvent.feed).also {
                    viewState.savedFeeds!!.remove(viewEvent.feed)
                    viewState = viewState.copy(loadSavedFeedsStatus = LoadSavedFeedsStatus.Loading)
                    feedRepository.loadSavedFeeds(viewEvent.context).also {
                        viewState = viewState.copy(savedFeeds = it as MutableList<RssFeed>, loadSavedFeedsStatus = LoadSavedFeedsStatus.Loaded)
                    }
                }
            }
        }
    }
}