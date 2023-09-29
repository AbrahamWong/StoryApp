package com.minotawr.storyapp.ui.base

import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.minotawr.storyapp.databinding.ActivityBaseToolbarBinding
import com.minotawr.storyapp.ui.welcome.WelcomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseToolbarActivity<VB : ViewBinding> :
    BaseStoryActivity<ActivityBaseToolbarBinding>() {

    private lateinit var _toolbarBinding: ViewBinding

    @Suppress("UNCHECKED_CAST")
    val toolbarBinding: VB
        get() = _toolbarBinding as VB

    abstract val toolbarInflater: (LayoutInflater) -> VB

    abstract fun setupPopup()

    open val viewModel: BaseToolbarViewModel by viewModel()

    open fun backButtonShown(): Boolean = false

    override val inflater: (LayoutInflater) -> ActivityBaseToolbarBinding
        get() = ActivityBaseToolbarBinding::inflate

    override fun setup() {
        _toolbarBinding = toolbarInflater.invoke(layoutInflater)
        binding.flContent.addView(toolbarBinding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(backButtonShown())

        setupPopup()
    }

    protected fun logout() {
        viewModel.logout().observe(this) {
            goToWelcomeActivity()
        }
    }

    fun goToWelcomeActivity() {
        Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(this)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressedDispatcher.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun showLoading() {
        binding.flProgress.isVisible = true
    }

    protected fun hideLoading() {
        binding.flProgress.isVisible = false
    }
}