package com.minotawr.storyapp.ui.home.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.minotawr.storyapp.databinding.AdapterHomeBinding
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.ui.home.adapter.HomeAdapterDelegate
import com.minotawr.storyapp.ui.home.adapter.HomeViewHolder

class HomePagingAdapter: PagingDataAdapter<Story, HomeViewHolder>(DIFF_CALLBACK) {

    var delegate: HomeAdapterDelegate? = null

    companion object {
        val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val data = getItem(position)

        if (data != null) {
            holder.assign(data)

            holder.itemView.setOnClickListener {
                delegate?.onItemClick(holder, data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = AdapterHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }
}