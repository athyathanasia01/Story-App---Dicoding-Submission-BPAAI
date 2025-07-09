package com.dicoding.storyapp.custom

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.dicoding.storyapp.databinding.DialogFragmentCustomBinding

class CustomDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentCustomBinding? = null
    private val binding get() = _binding!!

    private var title: String = "Default Title"
    private var message: String = "Default Message"
    private var buttonText: String = "OK"
    private var onButtonClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = title
        binding.message.text = message
        binding.btnPositive.text = buttonText

        binding.btnPositive.setOnClickListener {
            onButtonClick?.invoke()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    class Builder {
        private val fragment = CustomDialogFragment()

        fun setTitle(title: String): Builder {
            fragment.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            fragment.message = message
            return this
        }

        fun setButtonText(text: String): Builder {
            fragment.buttonText = text
            return this
        }

        fun setOnButtonClick(listener: () -> Unit): Builder {
            fragment.onButtonClick = listener
            return this
        }

        fun show(fragmentManager: FragmentManager) {
            fragment.show(fragmentManager, "CustomDialog")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}