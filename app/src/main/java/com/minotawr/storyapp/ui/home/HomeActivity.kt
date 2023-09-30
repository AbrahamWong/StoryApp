package com.minotawr.storyapp.ui.home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.minotawr.storyapp.R
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityHomeBinding
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.ui.add.AddStoryActivity
import com.minotawr.storyapp.ui.base.BaseToolbarActivity
import com.minotawr.storyapp.ui.detail.StoryDetailActivity
import com.minotawr.storyapp.ui.home.adapter.HomeAdapter
import com.minotawr.storyapp.ui.home.adapter.HomeAdapterDelegate
import com.minotawr.storyapp.ui.home.adapter.HomeViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseToolbarActivity<ActivityHomeBinding>() {
    override val toolbarInflater: (LayoutInflater) -> ActivityHomeBinding
        get() = ActivityHomeBinding::inflate

    private val storyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(toolbarBinding.root, "Story has been posted.", Snackbar.LENGTH_SHORT)
                .show()
            loadData()
        }
    }

    override val viewModel: HomeViewModel by viewModel()

    private val homeAdapter = HomeAdapter()

    override fun setupPopup() {
        title = getString(R.string.app_name)

        setupView()
        setupListener()
        loadData()      // TODO: why this triggered after returning from StoryDetailActivity
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

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        toolbarBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = homeAdapter
        }
    }

    private fun setupListener() {
        homeAdapter.delegate = object: HomeAdapterDelegate {
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
        viewModel.getStories().observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // do nothing
                }

                is Resource.Success -> {
                    val data = resource.data
                    if (data != null) {
                        homeAdapter.setStories(data)
                    }
                }

                is Resource.Unauthorized -> {
                    showError("Unauthorized access, logging out")
                    logout()
                }

                is Resource.Failed -> {
                    if (resource.message != null)
                        showError(resource.message)
                }
            }
        }
    }
}