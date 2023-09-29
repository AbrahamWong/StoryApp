package com.minotawr.storyapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.minotawr.storyapp.domain.model.Story

class StoryDiffUtil(
    private var old: List<Story>,
    private var new: List<Story>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition].id == new[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            old[oldItemPosition].id != new[newItemPosition].id ->
                false
            old[oldItemPosition].name != new[newItemPosition].name ->
                false
            old[oldItemPosition].description != new[newItemPosition].description ->
                false
            old[oldItemPosition].photoUrl != new[newItemPosition].photoUrl ->
                false
            old[oldItemPosition].createdAt != new[newItemPosition].createdAt ->
                false
            old[oldItemPosition].latitude != new[newItemPosition].latitude ->
                false
            old[oldItemPosition].longitude != new[newItemPosition].longitude ->
                false
            else -> true
        }
    }
}