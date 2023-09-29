package com.minotawr.storyapp.ui.splash

import android.content.Intent
import android.view.LayoutInflater
import com.minotawr.storyapp.databinding.ActivitySplashBinding
import com.minotawr.storyapp.ui.base.BaseStoryActivity
import com.minotawr.storyapp.ui.home.HomeActivity
import com.minotawr.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseStoryActivity<ActivitySplashBinding>() {
    override val inflater: (LayoutInflater) -> ActivitySplashBinding
        get() = ActivitySplashBinding::inflate

    private val viewModel: SplashViewModel by viewModel()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun setup() {
        loadData()
    }

    private fun loadData() {
        scope.launch { checkLogin() }
    }

    private suspend fun checkLogin() {
        viewModel.checkLogin().collect { isLoggedIn ->
            delay(1000L)

            val targetActivity = if (isLoggedIn) HomeActivity::class.java else WelcomeActivity::class.java
            startActivity(Intent(this, targetActivity))
            finish()
        }
    }
}