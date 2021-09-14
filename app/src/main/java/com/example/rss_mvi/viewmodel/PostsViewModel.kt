package com.example.rss_mvi.viewmodel

import android.app.Application
import android.util.Log
import android.util.Xml
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.rss_mvi.internal.HttpResponse
import com.example.rss_mvi.internal.TAG
import com.example.rss_mvi.internal.ViewModel
import com.example.rss_mvi.model.RssPost
import com.example.rss_mvi.repository.PostRepository
import com.example.rss_mvi.state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.URL

class PostsViewModel(app: Application) : ViewModel<PostsViewState, PostsViewEffect, PostsViewEvent>(app) {
    private val postRepository: PostRepository = PostRepository.getInstance()

    init {
        viewState = PostsViewState(
            loadCachedPostsStatus = LoadCachedPostsStatus.Idle,
            cachedPosts = null,
            fetchPostsStatus = FetchPostsStatus.Idle,
            fetchedPosts = null,
        )
    }

    override fun process(viewEvent: PostsViewEvent) {
        super.process(viewEvent)
        when (viewEvent) {
            is PostsViewEvent.LoadCachedPosts -> {
                viewState = viewState.copy(loadCachedPostsStatus = LoadCachedPostsStatus.Loading)

                viewModelScope.launch {
                    postRepository.loadCachedPostsById(viewEvent.context, viewEvent.feedId).also {
                        viewState = viewState.copy(cachedPosts = it, loadCachedPostsStatus = LoadCachedPostsStatus.Loaded)
                    }
                }
            }

            is PostsViewEvent.FetchPosts -> {
                viewState = viewState.copy(fetchPostsStatus = FetchPostsStatus.Fetching)
                if (!viewEvent.url.startsWith("http://") && !viewEvent.url.startsWith("https://")) viewEvent.url = "http://${viewEvent.url}"
                viewModelScope.launch {
                    when (val result = postRepository.getFeed(viewEvent.url)) {
                        is HttpResponse.Success -> {
                            result.data.items.forEach {
                                it.feedId = viewEvent.feedId
                                if (!postRepository.isPostCached(viewEvent.context, it)) postRepository.cachePost(viewEvent.context, it)
                            }
                            viewState = viewState.copy(fetchPostsStatus = FetchPostsStatus.Fetched, fetchedPosts = result.data.items)
                        }

                        is HttpResponse.Error -> {
                            viewState = viewState.copy(fetchPostsStatus = FetchPostsStatus.NotFetched)
                            viewEffect = PostsViewEffect.ShowSnackbar(result.message)
                        }
                    }
                }
            }
        }
    }
}