package com.minotawr.storyapp.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.minotawr.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private lateinit var showPasswordDrawable: Drawable
    private lateinit var hidePasswordDrawable: Drawable

    private var isPasswordVisible = false

    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        showPasswordDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
        hidePasswordDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable

        addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                if (text.toString().length < 8)
                    setError("Password should be at least 8 characters long")
                else {
                    error = null
                    setDrawables(endIconDrawable = hidePasswordDrawable)
                }
            }
        )

        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val drawableStart: Float
            val drawableEnd: Float
            var isDrawableClicked = false
            val currentDrawable = if (isPasswordVisible)
                showPasswordDrawable else hidePasswordDrawable

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                drawableEnd = (currentDrawable.intrinsicWidth + paddingStart).toFloat()
                if (event.x < drawableEnd) isDrawableClicked = true
            } else {
                drawableStart = (width - paddingEnd - currentDrawable.intrinsicWidth).toFloat()
                if (event.x > drawableStart) isDrawableClicked = true
            }

            if (isDrawableClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        if (isPasswordVisible)
                            hidePassword() else showPassword()

                        return true
                    }
                    else -> return false
                }
            } else return false
        }

        return false
    }

    private fun showPassword() {
        setDrawables(endIconDrawable = showPasswordDrawable)

        if (text != null)
            transformationMethod = null

        isPasswordVisible = true
    }

    private fun hidePassword() {
        setDrawables(endIconDrawable = hidePasswordDrawable)

        if (text != null)
            transformationMethod = PasswordTransformationMethod()

        isPasswordVisible = false
    }

    private fun setDrawables(
        startIconDrawable: Drawable? = null,
        topIconDrawable: Drawable? = null,
        endIconDrawable: Drawable? = null,
        bottomIconDrawable: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startIconDrawable,
            topIconDrawable,
            endIconDrawable,
            bottomIconDrawable
        )
    }
}