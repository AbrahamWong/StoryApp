package com.minotawr.storyapp.ui.home.adapter.paging

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.minotawr.storyapp.databinding.AdapterLoadingBinding

class LoadStateViewHolder(private val binding: AdapterLoadingBinding, retry: () -> Unit): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.btnRetry.setOnClickListener { retry.invoke() }
    }

    fun load(loadState: LoadState) {
        if (loadState is LoadState.Error)
            binding.tvError.text = loadState.error.localizedMessage?.replaceFirstChar(Char::titlecase)

        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.tvError.isVisible = loadState is LoadState.Error
        binding.btnRetry.isVisible = loadState is LoadState.Error
    }
}