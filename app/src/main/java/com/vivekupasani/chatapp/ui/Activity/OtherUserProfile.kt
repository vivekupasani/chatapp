package com.vivekupasani.chatapp.ui.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.ActivityOtherUserProfileBinding
import com.vivekupasani.chatapp.ui.fragment.Profile

class OtherUserProfile : AppCompatActivity() {

    lateinit var binding: ActivityOtherUserProfileBinding

    var username: String = ""
    var profilePic: String = ""
    var email: String = ""
    var about: String = ""
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtherUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fetchdatafromIntent()
        setOnProfile()

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun setOnProfile() {
        Glide.with(this).load(profilePic)
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.profile)
        binding.fullName.setText(username)
        binding.emailAddress.setText(email)
        binding.about.setText(about)
    }

    private fun fetchdatafromIntent() {
        username = intent.getStringExtra(Profile.Name) ?: ""
        profilePic = intent.getStringExtra(Profile.profilePic) ?: ""
        userId = intent.getStringExtra(Profile.userId) ?: ""
        about = intent.getStringExtra(Profile.About) ?: ""
        email = intent.getStringExtra(Profile.Email) ?: ""
    }
}