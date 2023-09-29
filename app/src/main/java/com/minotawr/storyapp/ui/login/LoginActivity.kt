package com.minotawr.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityLoginBinding
import com.minotawr.storyapp.ui.base.BaseStoryActivity
import com.minotawr.storyapp.ui.home.HomeActivity
import com.minotawr.storyapp.ui.register.RegisterActivity
import com.minotawr.storyapp.utils.CommonUtils.isValidEmail
import com.minotawr.storyapp.utils.CommonUtils.isValidPassword
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseStoryActivity<ActivityLoginBinding>() {
    override val inflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    private val viewModel: LoginViewModel by viewModel()

    override fun setup() {
        setupView()
        setupObserver()
        setupListener()
    }

    private fun setupView() {
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6_000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleAnim = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(1000)
        val subtitleAnim = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(1000)
        val emailAnim = ObjectAnimator.ofFloat(binding.llEmail, View.ALPHA, 1f).setDuration(1000)
        val passwordAnim = ObjectAnimator.ofFloat(binding.llPassword, View.ALPHA, 1f).setDuration(1000)
        val loginAnim = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(1000)
        val registerLabelAnim = ObjectAnimator.ofFloat(binding.tvRegisterLabel, View.ALPHA, 1f).setDuration(1000)
        val registerAnim = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            val playTogether = AnimatorSet().apply { playTogether(registerLabelAnim, registerAnim) }
            playSequentially(titleAnim, subtitleAnim, emailAnim, passwordAnim, loginAnim, playTogether)

            start()
        }
    }

    private fun setupObserver() {
        viewModel.email.observe(this) { _ ->
            binding.edLoginEmail.error = null
        }
    }

    private fun setupListener() {
        binding.edLoginEmail.addTextChangedListener { editable ->
            viewModel.setEmail(editable?.toString())
        }

        binding.edLoginPassword.addTextChangedListener { editable ->
            viewModel.setPassword(editable?.toString())
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvRegister.setOnClickListener { goToRegisterActivity() }
    }

    private fun login() {
        val email = viewModel.email.value
        val password = viewModel.password.value

        if (email == null || email.isValidEmail().not()) {
            binding.edLoginEmail.error = "Email is invalid"
            return
        }

        if (password == null || password.isValidPassword().not()) {
            binding.edLoginPassword.error = "Password must have at least one number and one capital letter"
            return
        }

        viewModel.login(email, password).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.flProgress.isVisible = true
                }

                is Resource.Success -> {
                    binding.flProgress.isVisible = false

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

                is Resource.Failed -> {
                    binding.flProgress.isVisible = false

                    if (resource.message != null)
                        showError(resource.message)
                }

                is Resource.Unauthorized -> {
                    binding.flProgress.isVisible = false

                    if (resource.message != null)
                        showError(resource.message)
                }
            }
        }
    }

    private fun goToRegisterActivity() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}