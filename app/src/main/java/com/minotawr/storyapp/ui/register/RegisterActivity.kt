package com.minotawr.storyapp.ui.register

import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.databinding.ActivityRegisterBinding
import com.minotawr.storyapp.ui.base.BaseStoryActivity
import com.minotawr.storyapp.ui.login.LoginActivity
import com.minotawr.storyapp.utils.CommonUtils.isValidEmail
import com.minotawr.storyapp.utils.CommonUtils.isValidPassword
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : BaseStoryActivity<ActivityRegisterBinding>() {
    override val inflater: (LayoutInflater) -> ActivityRegisterBinding
        get() = ActivityRegisterBinding::inflate

    private val viewModel: RegisterViewModel by viewModel()

    override fun setup() {
        setupView()
        setupObserver()
        setupListener()
    }

    private fun setupView() {
        // TODO: Setup animation
    }

    private fun setupObserver() {
        viewModel.name.observe(this) {
            binding.edRegisterName.error = null
        }

        viewModel.email.observe(this) {
            binding.edRegisterEmail.error = null
        }
    }

    private fun setupListener() {
        binding.edRegisterName.addTextChangedListener { editable ->
            viewModel.setName(editable.toString())
        }

        binding.edRegisterEmail.addTextChangedListener { editable ->
            viewModel.setEmail(editable.toString())
        }

        binding.edRegisterPassword.addTextChangedListener { editable ->
            viewModel.setPassword(editable.toString())
        }

        binding.btnRegister.setOnClickListener { validateInput() }

        binding.tvLogin.setOnClickListener {
            goToLoginActivity()
        }
    }

    private fun validateInput() {
        val name = viewModel.name.value
        val email = viewModel.email.value
        val password = viewModel.password.value

        if (name == null || name.length < 6) {
            binding.edRegisterName.error = "Name must be at least six characters long"
            return
        }

        if (email == null || email.isValidEmail().not()) {
            binding.edRegisterEmail.error = "Email is invalid"
            return
        }

        if (password == null || password.isValidPassword().not()) {
            binding.edRegisterPassword.error =
                "Password must have at least one number and one capital letter"
            return
        }

        register(name, email, password)
    }

    private fun register(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.flProgress.isVisible = true
                }

                is Resource.Success -> {
                    binding.flProgress.isVisible = false

                    AlertDialog.Builder(this@RegisterActivity)
                        .setTitle("Registration success!")
                        .setMessage("You have successfully registered. Log in now to use Story App!")
                        .setPositiveButton(
                            "Log in"
                        ) { dialog, _ ->
                            dialog?.dismiss()
                            goToLoginActivity()
                        }.create().show()
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

    private fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}