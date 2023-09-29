package com.minotawr.storyapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.minotawr.storyapp.databinding.AdapterHomeBinding
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.utils.StoryDiffUtil

class HomeAdapter : RecyclerView.Adapter<HomeViewHolder>() {

    var list: List<Story> = listOf()
        private set

    fun setStories(newData: List<Story>) {
        val diffUtil = StoryDiffUtil(list, newData)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        list = newData
        diffResult.dispatchUpdatesTo(this@HomeAdapter)
    }

    var delegate: HomeAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = AdapterHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.assign(list[position])

        holder.itemView.setOnClickListener {
            delegate?.onItemClick(holder, list[position])
        }
    }
}