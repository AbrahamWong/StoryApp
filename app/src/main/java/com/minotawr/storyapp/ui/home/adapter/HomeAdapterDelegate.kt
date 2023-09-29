package com.minotawr.storyapp.ui.home.adapter

import com.minotawr.storyapp.domain.model.Story

interface HomeAdapterDelegate {
    fun onItemClick(holder: HomeViewHolder, data: Story)
}