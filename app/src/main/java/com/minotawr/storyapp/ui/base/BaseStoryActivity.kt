package com.minotawr.storyapp.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BaseStoryActivity<V: ViewBinding>: AppCompatActivity() {

    private lateinit var _binding: ViewBinding
    @Suppress("UNCHECKED_CAST")
    protected val binding: V
        get() = _binding as V

    abstract val inflater: (LayoutInflater) -> V

    abstract fun setup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = inflater.invoke(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    protected fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        Log.d("Error", "showError: $message")
    }
}