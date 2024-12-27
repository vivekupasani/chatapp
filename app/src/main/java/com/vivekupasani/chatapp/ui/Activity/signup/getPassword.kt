package com.vivekupasani.chatapp.ui.Activity.signup

import Authentication
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.vivekupasani.chatapp.databinding.ActivityGetpasswordBinding
import com.vivekupasani.chatapp.ui.Activity.EditProfile
import com.vivekupasani.chatapp.ui.Activity.SignIn
import com.vivekupasani.chatapp.ui.fragment.Profile

class getPassword : AppCompatActivity() {

    private lateinit var binding: ActivityGetpasswordBinding
    private val viewModel: Authentication by viewModels()
    private lateinit var userPassword: String
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGetpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpObservers()
        onBackBtnClick()
        onGoToLoginBtnClick()
        proceedToSignUp()
    }

    private fun setUpObservers() {
        viewModel.signupStatus.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Sign Up successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EditProfile::class.java).apply {
                    putExtra("title", "Setup Profile")
                    putExtra(Profile.Email, userEmail)
                    putExtra(Profile.password, userPassword)
                }
                startActivity(intent)
                finishAffinity()
            }
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun onGoToLoginBtnClick() {
        binding.btnGologin.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }
    }

    private fun onBackBtnClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun proceedToSignUp() {
        binding.btnContinue2.setOnClickListener {
            userPassword = binding.emailPassword.text.trim().toString()
            userEmail = intent.getStringExtra("email").toString()


            if (userPassword.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            } else if (userPassword.length < 8) {
                binding.emailPassword.error = "Password must be 8 characters"
            } else if (binding.confirmPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Re-enter password", Toast.LENGTH_SHORT).show()
            } else if (binding.confirmPassword.text.toString() != userPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {

                if (userEmail != null) {
                    viewModel.signUpUser(userEmail, userPassword,)

                } else {
                    Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setToken() {

    }
}
