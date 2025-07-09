package com.dicoding.storyapp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R

class MyCustomLoginButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var textColor: Int
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable

    init {
        textColor = ContextCompat.getColor(context, R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_login_button) ?: throw RuntimeException("bg_login_button drawable not found")
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_login_button_disable) ?: throw RuntimeException("bg_login_button_disable drawable not found")

        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(textColor)
        textSize = 12f
        gravity = Gravity.CENTER
        isAllCaps = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
    }

}