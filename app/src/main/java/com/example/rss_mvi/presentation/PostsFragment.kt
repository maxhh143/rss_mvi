package com.example.rss_mvi.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alespero.expandablecardview.ExpandableCardView
import com.example.rss_mvi.R
import com.example.rss_mvi.internal.TAG
import com.example.rss_mvi.internal.ViewModelFragment
import com.example.rss_mvi.model.RssPost
import com.example.rss_mvi.state.*
import com.example.rss_mvi.utils.BackButtonPressed
import com.example.rss_mvi.utils.ProgressDialog
import com.example.rss_mvi.viewmodel.PostsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.extensions.LayoutContainer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class PostsFragment : ViewModelFragment<PostsViewState, PostsViewEffect, PostsViewEvent, PostsViewModel>(R.layout.fragment_posts) {
    override val viewModel: PostsViewModel by viewModels()

    private val postsListAdapter: PostsListAdapter by lazy { PostsListAdapter(postsListRecyclerView) }

    private lateinit var postsListRecyclerView: RecyclerView
    private lateinit var noPostsTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noPostsTextView = view.findViewById(R.id.noPostsTextView)
        postsListRecyclerView = view.findViewById(R.id.postsListRecyclerView)
        postsListRecyclerView.adapter = postsListAdapter

        view.findViewById<Toolbar>(R.id.postsToolbar).apply {
            title = requireArguments().getString("feedName")
            navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_arrow_left)
            setNavigationOnClickListener { findNavController().navigate(R.id.action_postsFragment_to_homeFragment) }
        }

        getConnectionType(requireActivity().applicationContext).also {
            if (it == 0) {
                viewModel.process(PostsViewEvent.LoadCachedPosts(
                    requireActivity(),
                    requireArguments().getInt("feedId"),
                ))
                return
            }

            viewModel.process(PostsViewEvent.FetchPosts(
                requireActivity(),
                requireArguments().getString("feedUrl")!!,
                requireArguments().getInt("feedId"),
            ))
        }
    }

    override fun renderViewState(viewState: PostsViewState) {
        when (viewState.loadCachedPostsStatus) {
            LoadCachedPostsStatus.Idle -> Log.d(TAG, "LoadCachedPostsStatus.Idle")

            LoadCachedPostsStatus.Loading -> ProgressDialog.show(requireActivity(), "Загружаю сохранённые посты\nПожалуйста, подождите")

            LoadCachedPostsStatus.Loaded -> {
                ProgressDialog.dismiss()
                if (viewState.cachedPosts != null && viewState.cachedPosts.isNotEmpty()) {
                    postsListAdapter.submitList(viewState.cachedPosts)
                    return
                }

                postsListRecyclerView.visibility = View.GONE
                noPostsTextView.visibility = View.VISIBLE
            }
        }

        when (viewState.fetchPostsStatus) {
            is FetchPostsStatus.Idle -> Log.d(TAG, "FetchPostsStatus.Idle")

            is FetchPostsStatus.Fetching -> ProgressDialog.show(requireActivity(), "Загружаю посты\nПожалуйста, подождите")

            is FetchPostsStatus.Fetched -> {
                ProgressDialog.dismiss()
                if (viewState.fetchedPosts != null && viewState.fetchedPosts.isNotEmpty()) {
                    postsListAdapter.submitList(viewState.fetchedPosts)
                    return
                }

                postsListRecyclerView.visibility = View.GONE
                noPostsTextView.visibility = View.VISIBLE
            }

            is FetchPostsStatus.NotFetched -> ProgressDialog.dismiss()
        }
    }

    override fun renderViewEffect(viewEffect: PostsViewEffect) {
        when (viewEffect) {
            is PostsViewEffect.ShowSnackbar -> Snackbar.make(
                requireActivity().findViewById(R.id.fragment_posts),
                viewEffect.message,
                Snackbar.LENGTH_SHORT,
            ).show()
        }
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @IntRange(from = 0, to = 3)
    private fun getConnectionType(context: Context): Int {
        var result = 0
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 1
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 2
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> 3
                    else -> 0
                }
            }
        }
        return result
    }

    @Subscribe
    fun onBackButtonPressed(event: BackButtonPressed) {
        findNavController().navigate(R.id.action_postsFragment_to_homeFragment)
    }

    private class PostsListAdapter(val transitionContainer: RecyclerView) : ListAdapter<RssPost, PostsListAdapter.ViewHolder>(DifferenceCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_post, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
            fun bind(item: RssPost) = with(item) {
                val descriptionContainer = containerView.findViewById<LinearLayout>(R.id.postDescriptionContainerLinearLayout)
                val arrow = containerView.findViewById<ImageView>(R.id.postArrowDownImageView)

                containerView.findViewById<TextView>(R.id.postTitleTextView).text = title
                containerView.findViewById<TextView>(R.id.postDescriptionTextView).text = description

                containerView.setOnClickListener {
                    when (descriptionContainer.visibility) {
                        View.GONE -> {
                            TransitionManager.beginDelayedTransition(transitionContainer, AutoTransition())
                            descriptionContainer.visibility = View.VISIBLE
                            arrow.setImageResource(R.drawable.ic_arrow_up)
                        }

                        View.VISIBLE -> {
                            TransitionManager.beginDelayedTransition(transitionContainer, AutoTransition())
                            descriptionContainer.visibility = View.GONE
                            arrow.setImageResource(R.drawable.ic_arrow_down)
                        }

                        else -> { }
                    }
                }
            }
        }

        class DifferenceCallback : DiffUtil.ItemCallback<RssPost>() {
            override fun areItemsTheSame(oldItem: RssPost, newItem: RssPost): Boolean = oldItem.description == newItem.description

            override fun areContentsTheSame(oldItem: RssPost, newItem: RssPost): Boolean = oldItem.description == newItem.description
        }
    }
}