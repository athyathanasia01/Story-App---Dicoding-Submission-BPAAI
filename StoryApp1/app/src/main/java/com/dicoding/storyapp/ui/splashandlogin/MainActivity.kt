package com.dicoding.storyapp.ui.splashandlogin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dicoding.storyapp.R
import com.dicoding.storyapp.custom.CustomDialogFragment
import com.dicoding.storyapp.custom.MyCustomLoginButton
import com.dicoding.storyapp.custom.MyCustomRegisterButton
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.model.UserModel
import com.dicoding.storyapp.network.Result
import com.dicoding.storyapp.preferences.ThemeViewModel
import com.dicoding.storyapp.preferences.UserPreferences
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.liststory.ListStoryActivity
import com.dicoding.storyapp.ui.register.RegisterActivity
import com.dicoding.storyapp.utils.isValidEmail
import com.dicoding.storyapp.utils.isValidPassword

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loginButton: MyCustomLoginButton
    private lateinit var registerButton: MyCustomRegisterButton

    private lateinit var factory: ViewModelFactory
    private val viewModel: LoginViewModel by viewModels { factory }
    private val themeViewModel : ThemeViewModel by viewModels { factory }

    private lateinit var userModel : UserModel
    private lateinit var preferences: UserPreferences

    companion object {
        var hasSplashShown = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.dicoding_PrimaryAccent)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

        registerButton.setOnClickListener { registerButtonHandler() }

        preferences = UserPreferences(this)
        userModel = preferences.getSession()

        val motionLayout = binding.motionLayout
        if (hasSplashShown) {
            motionLayout.setProgress(1f)
            motionLayout.transitionToState(R.id.login)

            checkEmailTextField()
            checkPasswordTextField()

            loginButton.setOnClickListener { loginButtonHandler() }
        } else {
            if (userModel.token != null) {
                val intent = Intent(this, ListStoryActivity::class.java)
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                    finish()
                }, 10000)
            }

            motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout,
                    startId: Int,
                    endId: Int
                ) {}

                override fun onTransitionChange(
                    motionLayout: MotionLayout,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
                    setButtonLoginEnabled()
                }

                override fun onTransitionCompleted(
                    motionLayout: MotionLayout,
                    currentId: Int
                ) {
                    if (currentId == R.id.end) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            motionLayout.transitionToState(R.id.login)
                            hasSplashShown = true
                        }, 10_000)
                    }

                    checkEmailTextField()
                    checkPasswordTextField()

                    loginButton.setOnClickListener { loginButtonHandler() }
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                    setButtonLoginEnabled()
                }
            })


        }
    }

    private fun registerButtonHandler() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun checkEmailTextField() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setButtonLoginEnabled()
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
                    setButtonLoginEnabled()
                } else {
                    binding.emailEditTextLayout.error = getString(R.string.handle_empty_field)
                }
            }
        })
    }

    private fun checkPasswordTextField() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setButtonLoginEnabled()
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
                    setButtonLoginEnabled()
                } else {
                    binding.passwordEditTextLayout.error = getString(R.string.handle_empty_field)
                }
            }
        })
    }

    private fun setButtonLoginEnabled() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        loginButton.isEnabled = email.isValidEmail() && password.isValidPassword()
    }

    private fun loginButtonHandler() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        viewModel.saveSessionLogin(email, password).observe(this) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> { showLoading(true) }
                    is Result.Success -> {
                        showLoading(false)
                        val loginModel = UserModel(
                            result.data.loginResult.userId,
                            result.data.loginResult.name,
                            result.data.loginResult.token
                        )

                        preferences.saveSession(loginModel)

                        val intent = Intent(this, ListStoryActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Failed -> {
                        showLoading(false)
                        showCustomDialog(
                            "Error",
                            result.data,
                            "Close",
                            {
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }

    private fun showCustomDialog(title: String, message: String, buttonText: String, action: () -> Unit) {
        CustomDialogFragment.Builder()
            .setTitle(title)
            .setMessage(message)
            .setButtonText(buttonText)
            .setOnButtonClick { action }
            .show(supportFragmentManager)
    }
}