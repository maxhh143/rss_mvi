package com.example.rss_mvi.internal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

abstract class ViewModelFragment<STATE, EFFECT, EVENT, VM : ViewModel<STATE, EFFECT, EVENT>>(layoutId: Int) : Fragment(layoutId) {
    abstract val viewModel: VM

    private val viewStateObserver: Observer<STATE> = Observer<STATE> {
        Log.d(TAG, "Observed state: $it")
        renderViewState(it)
    }

    private val viewEffectObserver: Observer<EFFECT> = Observer<EFFECT> {
        Log.d(TAG, "Observed effect: $it")
        renderViewEffect(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.viewStates().observe(viewLifecycleOwner, viewStateObserver)
        viewModel.viewEffects().observe(viewLifecycleOwner, viewEffectObserver)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun renderViewState(viewState: STATE)

    abstract fun renderViewEffect(viewEffect: EFFECT)
}