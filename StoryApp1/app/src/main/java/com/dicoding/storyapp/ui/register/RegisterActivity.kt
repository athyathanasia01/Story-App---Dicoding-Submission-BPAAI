package com.dicoding.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.storyapp.custom.MyCustomLoginButton
import com.dicoding.storyapp.custom.MyCustomRegisterButton
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.network.Result
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.splashandlogin.MainActivity
import com.dicoding.storyapp.utils.isValidEmail
import com.dicoding.storyapp.utils.isValidPassword
import com.dicoding.storyapp.R
import com.dicoding.storyapp.custom.CustomDialogFragment
import com.dicoding.storyapp.preferences.ThemeViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var loginButton: MyCustomRegisterButton
    private lateinit var registerButton: MyCustomLoginButton

    private lateinit var factory: ViewModelFactory
    private val viewModel : RegisterViewModel by viewModels { factory }
    private val themeViewModel : ThemeViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getFactoryInstance(application)

        showLoading(false)

        themeViewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        loginButton = binding.loginButton
        registerButton = binding.registerButton

        checkNameTextField()
        checkEmailTextField()
        checkPasswordTextField()

        registerButton.setOnClickListener { registerButtonHandler() }
        loginButton.setOnClickListener { loginButtonHandler() }
    }

    private fun showCustomDialog(title: String, message: String, buttonText: String, action: () -> Unit) {
        CustomDialogFragment.Builder()
            .setTitle(title)
            .setMessage(message)
            .setButtonText(buttonText)
            .setOnButtonClick { action }
            .show(supportFragmentManager)
    }

    private fun loginButtonHandler() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun registerButtonHandler() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        viewModel.userRegister(name, email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)

                        showCustomDialog(
                            "Add User Success",
                            result.data.message,
                            "Close",
                            {
                                binding.nameEditText.text?.clear()
                                binding.nameEditText.requestFocus()

                                binding.emailEditText.text?.clear()
                                binding.emailEditText.requestFocus()

                                binding.passwordEditText.text?.clear()
                                binding.passwordEditText.requestFocus()
                            }
                        )

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Failed -> {
                        showLoading(false)
                        showCustomDialog(
                            "Add User Failed",
                            result.data,
                            "Close",
                            {
                                binding.nameEditText.text?.clear()
                                binding.nameEditText.requestFocus()

                                binding.emailEditText.text?.clear()
                                binding.emailEditText.requestFocus()

                                binding.passwordEditText.text?.clear()
                                binding.passwordEditText.requestFocus()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun checkNameTextField() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().isNotEmpty()) {
                    binding.nameEditTextLayout.error = null
                    setRegisterButtonEnable()
                } else {
                    binding.nameEditTextLayout.error = getString(R.string.handle_empty_field)
                }
            }
        })
    }

    private fun checkEmailTextField() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().isNotEmpty()) {
                    binding.emailEditTextLayout.error = null
                    setRegisterButtonEnable()
                } else {
                    binding.emailEditTextLayout.error = getString(R.string.handle_empty_field)
                }
            }
        })
    }

    private fun checkPasswordTextField() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().isNotEmpty()) {
                    binding.passwordEditTextLayout.error = null
                    setRegisterButtonEnable()
                } else {
                    binding.passwordEditTextLayout.error = getString(R.string.handle_empty_field)
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }

    private fun setRegisterButtonEnable() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        registerButton.isEnabled = name.isNotEmpty() && email.isValidEmail() && password.isValidPassword()
    }
}