package com.dicoding.storyapp.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R

@SuppressLint("ClickableViewAccessibility")
class MyCustomRegisterButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {
    private var textEnablePressedColor: Int = 0
    private var textEnableNormalColor: Int = 0
    private var textDisableColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable

    init {
        textEnableNormalColor = ContextCompat.getColor(context, R.color.dicoding_PrimaryAccent)
        textEnablePressedColor = ContextCompat.getColor(context, R.color.white)
        textDisableColor = ContextCompat.getColor(context, R.color.dicoding_SecondaryAccent)

        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_register_button) ?: throw RuntimeException("bg_register_button drawable not found")
        disabledBackground = ContextCompat.getDrawable( context, R.drawable.bg_register_button_disable) ?: throw RuntimeException("bg_register_button_disable drawable not found")

        background = if (isEnabled) enabledBackground else disabledBackground
        textSize = 12f
        gravity = Gravity.CENTER
        isAllCaps = true

        // Handler manual sebagai fallback
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> refreshDrawableState()
                MotionEvent.ACTION_UP -> refreshDrawableState()
                MotionEvent.ACTION_CANCEL -> refreshDrawableState()
            }
            false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(
            if (isPressed) textEnablePressedColor
            else if (!isEnabled) textDisableColor
            else textEnableNormalColor
        )
    }
}