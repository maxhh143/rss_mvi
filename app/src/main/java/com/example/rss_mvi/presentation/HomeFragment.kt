package com.example.rss_mvi.presentation

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rss_mvi.R
import com.example.rss_mvi.internal.ViewModelFragment
import com.example.rss_mvi.model.RssFeed
import com.example.rss_mvi.state.HomeViewEffect
import com.example.rss_mvi.state.HomeViewEvent
import com.example.rss_mvi.state.HomeViewState
import com.example.rss_mvi.state.LoadSavedFeedsStatus
import com.example.rss_mvi.utils.ProgressDialog
import com.example.rss_mvi.viewmodel.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.extensions.LayoutContainer

class HomeFragment : ViewModelFragment<HomeViewState, HomeViewEffect, HomeViewEvent, HomeViewModel>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by viewModels()

    private val feedsListAdapter: FeedsListAdapter by lazy { FeedsListAdapter(
        requireActivity(),
        { findNavController().navigate(
            R.id.action_homeFragment_to_postsFragment,
            bundleOf("feedId" to it.id, "feedName" to it.name, "feedUrl" to it.link)
        ) },
        { viewModel.process(HomeViewEvent.DeleteSavedFeed(requireActivity(), it)) }
    ) }

    private lateinit var noFeedsSaved: TextView
    private lateinit var feedsList: RecyclerView

    companion object {
        private const val PREFERENCES_NAME: String = "com.example.rss_mvi.SHARED_PREFERENCES"
        private const val FIRST_LAUNCH: String = "FIRST_LAUNCH"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noFeedsSaved = view.findViewById(R.id.noFeedsSavedTextView)
        feedsList = view.findViewById(R.id.postsListRecyclerView)

        feedsList.adapter = feedsListAdapter

        view.findViewById<FloatingActionButton>(R.id.addFeedFloatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFeedFragment)
        }

        requireActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).apply {
            when (getBoolean(FIRST_LAUNCH, true)) {
                true -> {
                    edit().also {
                        it.remove(FIRST_LAUNCH)
                        it.putBoolean(FIRST_LAUNCH, false)
                    }.apply()

                    viewModel.process(HomeViewEvent.SaveInitialFeeds(requireActivity()))
                }

                false -> {
                    viewModel.process(HomeViewEvent.LoadSavedFeeds(requireActivity()))
                }
            }
        }
    }

    override fun renderViewState(viewState: HomeViewState) {
        when (viewState.loadSavedFeedsStatus) {
            is LoadSavedFeedsStatus.Loading -> {
                ProgressDialog.show(requireActivity(), "Пожалуйста, подождите")
            }

            is LoadSavedFeedsStatus.Reloading -> {
                ProgressDialog.show(requireActivity(), "Пожалуйста, подождите")
                feedsListAdapter.notifyDataSetChanged()
            }

            is LoadSavedFeedsStatus.Loaded -> {
                ProgressDialog.dismiss()

                if (viewState.savedFeeds!!.isEmpty()) {
                    noFeedsSaved.visibility = View.VISIBLE
                    return
                }

                feedsListAdapter.submitList(viewState.savedFeeds)
                feedsList.visibility = View.VISIBLE
            }
        }
    }

    override fun renderViewEffect(viewEffect: HomeViewEffect) {
        when (viewEffect) {
            is HomeViewEffect.ShowSnackBar -> Snackbar.make(
                requireActivity().findViewById(R.id.fragment_home),
                viewEffect.message,
                Snackbar.LENGTH_SHORT,
            ).show()
        }
    }

    private class FeedsListAdapter(
        val context: Context,
        val onClickCallback: (RssFeed) -> Unit,
        val onLongClickCallback: (RssFeed) -> Unit,
    ) : ListAdapter<RssFeed, FeedsListAdapter.ViewHolder>(DifferenceCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_feed, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
            fun bind(item: RssFeed) = with(item) {
                containerView.findViewById<TextView>(R.id.feedTitleTextView).text = name
                containerView.findViewById<TextView>(R.id.feedLinkTextView).text = link
                containerView.setOnClickListener { onClickCallback(this) }
                containerView.setOnLongClickListener {
                    AlertDialog.Builder(context, R.style.alert_dialog_white)
                        .setMessage("Удалить подписку на $name?")
                        .setCancelable(true)
                        .setPositiveButton("Да") { dialog: DialogInterface, _ ->
                            onLongClickCallback(this)
                            dialog.cancel()
                        }
                        .setNegativeButton("Нет") { dialog: DialogInterface, _ -> dialog.cancel() }
                        .create()
                        .show()
                    true
                }
            }
        }

        class DifferenceCallback : DiffUtil.ItemCallback<RssFeed>() {
            override fun areItemsTheSame(oldItem: RssFeed, newItem: RssFeed): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RssFeed, newItem: RssFeed): Boolean = oldItem.link == newItem.link
        }
    }
}