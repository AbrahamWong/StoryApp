package com.minotawr.storyapp.ui.detail

import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityStoryDetailBinding
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.ui.base.BaseToolbarActivity
import com.minotawr.storyapp.utils.CommonUtils.getAppropriatePostTime
import com.minotawr.storyapp.utils.CommonUtils.toDate
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoryDetailActivity : BaseToolbarActivity<ActivityStoryDetailBinding>() {
    override val toolbarInflater: (LayoutInflater) -> ActivityStoryDetailBinding
        get() = ActivityStoryDetailBinding::inflate

    override fun backButtonShown(): Boolean = true

    override val viewModel: StoryDetailViewModel by viewModel()

    companion object {
        const val EXTRA_ID = "id"
    }

    override fun setupPopup() {
        setupView()
        loadData()
    }

    private fun setupView() {
        // TODO("Not yet implemented")
    }

    private fun loadData() {
        val storyId = intent.getStringExtra(EXTRA_ID)
        if (storyId != null)
            getStoryDetail(storyId)
    }

    private fun getStoryDetail(id: String) {
        viewModel.getStoryDetail(id).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // do nothing
                }

                is Resource.Unauthorized -> {
                    if (resource.message != null) {
                        showError(resource.message)
                        logout()
                    }
                }

                is Resource.Success -> {
                    val data = resource.data
                    if (data != null)
                        showStory(data)
                }

                is Resource.Failed -> {
                    if (resource.message != null)
                        showError(resource.message)
                }
            }
        }
    }

    private fun showStory(data: Story) {
        with (toolbarBinding) {
            Glide.with(this.root)
                .load(data.photoUrl)
                .into(ivDetailPhoto)

            tvDetailTime.text = data.createdAt.toDate()?.getAppropriatePostTime(this@StoryDetailActivity)
            tvDetailName.text = data.name
            tvDetailDescription.text = data.description
        }
    }
}