package com.minotawr.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import com.minotawr.storyapp.databinding.ActivityWelcomeBinding
import com.minotawr.storyapp.ui.base.BaseStoryActivity
import com.minotawr.storyapp.ui.login.LoginActivity
import com.minotawr.storyapp.ui.register.RegisterActivity

class WelcomeActivity : BaseStoryActivity<ActivityWelcomeBinding>() {

    override val inflater: (LayoutInflater) -> ActivityWelcomeBinding
        get() = ActivityWelcomeBinding::inflate

    override fun setup() {
        setupView()
        setupListener()
    }

    private fun setupView() {
        ObjectAnimator.ofFloat(binding.imgWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6_000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleAnim = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA,  1f).setDuration(1000)
        val subtitleAnim = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA,  1f).setDuration(1000)
        val loginAnim = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA,  1f).setDuration(1000)
        val registerAnim = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA,  1f).setDuration(1000)

        AnimatorSet().apply {
            val playTogether = AnimatorSet().apply { playTogether(loginAnim, registerAnim) }
            playSequentially(titleAnim, subtitleAnim, playTogether)

            start()
        }
    }

    private fun setupListener() {
        with (binding) {
            btnLogin.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                finish()
            }

            btnRegister.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }
}