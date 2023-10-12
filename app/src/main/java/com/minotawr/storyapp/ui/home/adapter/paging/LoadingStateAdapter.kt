package com.minotawr.storyapp.ui.home.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.minotawr.storyapp.databinding.AdapterLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.load(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder =
        LoadStateViewHolder(
            AdapterLoadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            retry
        )
}