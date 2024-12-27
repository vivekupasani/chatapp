package com.vivekupasani.chatapp.ui.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.MainActivity
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.ActivityEditprofileBinding
import com.vivekupasani.chatapp.ui.fragment.Profile
import com.vivekupasani.chatapp.viewModels.EditProfileViewModel

@Suppress("DEPRECATION")
class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditprofileBinding
    private var imageURI: Uri? = null

    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var name: String
    private lateinit var about: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var profilePic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.text = intent.getStringExtra("title")

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupObservers()
        setValuesFromIntent()
        onProfileBtnClick()
        onSaveBtnClick()
    }

    private fun setupObservers() {
        viewModel.edited.observe(this, Observer {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        })
        viewModel.error.observe(
            this,
            Observer {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun setValuesFromIntent() {
        name = intent.getStringExtra(Profile.Name).orEmpty()
        email = intent.getStringExtra(Profile.Email).orEmpty()
        password = intent.getStringExtra(Profile.password).orEmpty()
        about = intent.getStringExtra(Profile.About).orEmpty()
        profilePic = intent.getStringExtra(Profile.profilePic).orEmpty()

        binding.fullName.setText(name)
        binding.about.setText(about)
        binding.emailAddress.setText(email)
        Glide.with(this).load(profilePic)
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.profile)
    }

    private fun onSaveBtnClick() {
        binding.btnSave.setOnClickListener {
            viewModel.editProfile(
                imageURI,
                binding.fullName.text.toString(),
                binding.emailAddress.text.toString(),
                password,
                binding.about.text.toString()

            )
        }
    }

    private fun onProfileBtnClick() {
        binding.profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 45 && data != null) {
            imageURI = data.data
            Glide.with(this).load(imageURI).placeholder(R.drawable.profile).into(binding.profile)
        }
    }
}
