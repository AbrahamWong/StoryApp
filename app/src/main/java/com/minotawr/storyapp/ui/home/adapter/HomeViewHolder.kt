package com.minotawr.storyapp.ui.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minotawr.storyapp.databinding.AdapterHomeBinding
import com.minotawr.storyapp.domain.model.Story

class HomeViewHolder(private val binding: AdapterHomeBinding): RecyclerView.ViewHolder(binding.root) {

    val ivItemPhoto = binding.ivItemPhoto
    val tvItemName = binding.tvItemName
    val tvItemDescription = binding.tvItemDescription

    fun assign(data: Story) {
        Glide.with(itemView.context)
            .load(data.photoUrl)
            .into(binding.ivItemPhoto)

        binding.tvItemName.text = data.name
        binding.tvItemDescription.text = data.description
    }
}