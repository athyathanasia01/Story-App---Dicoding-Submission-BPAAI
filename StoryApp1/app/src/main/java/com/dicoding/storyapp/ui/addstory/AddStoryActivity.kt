package com.dicoding.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.R
import com.dicoding.storyapp.custom.CustomDialogFragment
import com.dicoding.storyapp.network.Result
import com.dicoding.storyapp.preferences.ThemeViewModel
import com.dicoding.storyapp.preferences.UserPreferences
import com.dicoding.storyapp.ui.liststory.ListStoryActivity
import com.dicoding.storyapp.utils.getImageUri
import com.dicoding.storyapp.utils.toFile

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var factory: ViewModelFactory
    private val viewModel: AddStoryViewModel by viewModels { factory }
    private val themeViewModel : ThemeViewModel by viewModels { factory }

    private lateinit var preferences: UserPreferences

    private var currentImageUri: Uri? = null

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
            Log.d("Camera take picture", "picture added")
        } else {
            currentImageUri = null
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                this,
                "Permission request granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                "Permission request denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getFactoryInstance(application)

        preferences = UserPreferences(this)

        showLoading(false)

        themeViewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnToGallery.setOnClickListener { galleryButtonHandler() }
        binding.btnToCamera.setOnClickListener { cameraButtonHandler() }
        binding.btnSubmit.setOnClickListener { submitButtonHandler() }

        checkDescriptionTextField()
    }

    private fun showCustomDialog(title: String, message: String, buttonText: String, action: () -> Unit) {
        CustomDialogFragment.Builder()
            .setTitle(title)
            .setMessage(message)
            .setButtonText(buttonText)
            .setOnButtonClick { action }
            .show(supportFragmentManager)
    }

    private fun galleryButtonHandler() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun cameraButtonHandler() {
        currentImageUri = getImageUri(this)
        launchIntentCamera.launch(currentImageUri!!)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Show Image by Uri", "showImage: $it")
            binding.storyPicture.setImageURI(it)
        }
    }

    private fun checkDescriptionTextField() {
        binding.inputTextDescription.addTextChangedListener(object : TextWatcher {
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
                    setSubmitButtonEnabled()
                }
            }
        })
    }

    private fun submitButtonHandler() {
        currentImageUri?.let { uri ->
            val imageFile = uri.toFile(this)
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.inputTextDescription.text.toString()

            val token = preferences.getSession().token!!

            viewModel.addNewStory(token, description, imageFile).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)

                            val intent = Intent(this@AddStoryActivity, ListStoryActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        is Result.Failed -> {
                            showLoading(false)
                            showCustomDialog(
                                "Add New Failed",
                                result.data,
                                "Close",
                                { }
                            )
                        }
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

    private fun setSubmitButtonEnabled() {
        val imageStory = binding.storyPicture.drawable
        val defaultImage = ContextCompat.getDrawable(this, R.drawable.pict_init)
        val description = binding.inputTextDescription.text.toString()

        binding.btnSubmit.isEnabled = imageStory != null && imageStory != defaultImage && description.isNotEmpty() && binding.inputTextDescription.lineCount > 1
    }
}