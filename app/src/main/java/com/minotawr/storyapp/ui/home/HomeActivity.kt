package com.minotawr.storyapp.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.snackbar.Snackbar
import com.minotawr.storyapp.R
import com.minotawr.storyapp.databinding.ActivityHomeBinding
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.ui.add.AddStoryActivity
import com.minotawr.storyapp.ui.base.BaseToolbarActivity
import com.minotawr.storyapp.ui.detail.StoryDetailActivity
import com.minotawr.storyapp.ui.home.adapter.HomeAdapterDelegate
import com.minotawr.storyapp.ui.home.adapter.HomeViewHolder
import com.minotawr.storyapp.ui.home.adapter.paging.HomePagingAdapter
import com.minotawr.storyapp.ui.home.adapter.paging.LoadingStateAdapter
import com.minotawr.storyapp.ui.map.MapsStoryActivity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseToolbarActivity<ActivityHomeBinding>() {
    override val toolbarInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    private val storyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(toolbarBinding.root, "Story has been posted.", Snackbar.LENGTH_SHORT)
                .show()

            viewModel.hasNotScrolled.value = true
            loadData()
        }
    }

    override val viewModel: HomeViewModel by viewModel()

    private val homePagingAdapter = HomePagingAdapter()

    override fun setupPopup() {
        title = getString(R.string.app_name)

        setupView()
        setupObserver()
        setupListener()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                true
            }

            R.id.action_logout -> {
                AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Logout") { dialog, _ ->
                        dialog.dismiss()
                        logout()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

                true
            }

            R.id.action_language -> {
                val settingIntent = Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS)
                startActivity(settingIntent)
                true
            }

            R.id.action_map -> {
                startActivity(Intent(this, MapsStoryActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        toolbarBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = homePagingAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    homePagingAdapter.retry()
                }
            )
        }
    }

    private fun setupObserver() {
        viewModel.pagedStories.observe(this) { stories ->
            homePagingAdapter.submitData(lifecycle, stories)
        }

        toolbarBinding.recyclerView.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0)
                    viewModel.hasNotScrolled.value = false
            }
        })

        val isNotLoading = homePagingAdapter.loadStateFlow
            .distinctUntilChangedBy { it.source.refresh }
            .map { it.source.refresh is LoadState.NotLoading }

        val shouldScrollToTop = combine(
            isNotLoading,
            viewModel.hasNotScrolled,
            Boolean::and
        ).distinctUntilChanged()

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll)
                    toolbarBinding.recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun setupListener() {
        homePagingAdapter.delegate = object: HomeAdapterDelegate {
            override fun onItemClick(holder: HomeViewHolder, data: Story) {
                val intent = Intent(this@HomeActivity, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.EXTRA_ID, data.id)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HomeActivity,
                    Pair(holder.ivItemPhoto, "photo"),
                    Pair(holder.tvItemName, "name"),
                    Pair(holder.tvItemDescription, "description"),
                )

                startActivity(intent, optionsCompat.toBundle())
            }
        }

        toolbarBinding.fabAdd.setOnClickListener {
            storyLauncher.launch(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun loadData() {
        getStories()
    }

    private fun getStories() {
        lifecycleScope.launch {
            viewModel.getPagedStories().collect { pagingData ->
                viewModel.pagedStories.postValue(pagingData)
            }
        }
    }
}