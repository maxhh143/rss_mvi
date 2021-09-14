package com.example.rss_mvi.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rss_mvi.R
import com.example.rss_mvi.internal.ViewModelFragment
import com.example.rss_mvi.model.RssFeed
import com.example.rss_mvi.state.AddFeedViewEffect
import com.example.rss_mvi.state.AddFeedViewEvent
import com.example.rss_mvi.state.AddFeedViewState
import com.example.rss_mvi.state.SaveFeedStatus
import com.example.rss_mvi.utils.BackButtonPressed
import com.example.rss_mvi.utils.ProgressDialog
import com.example.rss_mvi.viewmodel.AddFeedViewModel
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AddFeedFragment : ViewModelFragment<AddFeedViewState, AddFeedViewEffect, AddFeedViewEvent, AddFeedViewModel>(R.layout.fragment_add_feed) {
    override val viewModel: AddFeedViewModel by viewModels()

    private lateinit var feedNameEditText: EditText
    private lateinit var feedLinkEditText: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedNameEditText = view.findViewById(R.id.feedNameEditText)
        feedLinkEditText = view.findViewById(R.id.feedLinkEditText)

        view.findViewById<Toolbar>(R.id.addFeedToolbar).apply {
            navigationIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_arrow_left)
            setNavigationOnClickListener { findNavController().navigate(R.id.action_addFeedFragment_to_homeFragment) }
        }

        view.findViewById<Button>(R.id.saveFeedButton).setOnClickListener {
            viewModel.process(AddFeedViewEvent.SaveFeed(requireActivity(), RssFeed(
                id = null,
                name = feedNameEditText.text.toString(),
                link = feedLinkEditText.text.toString(),
            )))
        }
    }

    override fun renderViewState(viewState: AddFeedViewState) {
        when (viewState.saveFeedStatus) {
            is SaveFeedStatus.Idle -> {

            }

            is SaveFeedStatus.Saving -> ProgressDialog.show(requireActivity(), "Сохраняю\nПожалуйста, подождите")

            is SaveFeedStatus.Saved -> {
                ProgressDialog.dismiss()
                findNavController().navigate(R.id.action_addFeedFragment_to_homeFragment)
            }
        }
    }

    override fun renderViewEffect(viewEffect: AddFeedViewEffect) {
        when (viewEffect) {
            is AddFeedViewEffect.ShowSnackBar -> Snackbar.make(
                requireActivity().findViewById(R.id.fragmet_add_feed),
                viewEffect.message,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @Subscribe
    fun onBackButtonPressed(event: BackButtonPressed) {
        findNavController().navigate(R.id.action_addFeedFragment_to_homeFragment)
    }
}