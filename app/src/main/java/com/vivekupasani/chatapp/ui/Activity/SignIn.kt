package com.vivekupasani.chatapp.ui.Activity

import Authentication
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.vivekupasani.chatapp.MainActivity
import com.vivekupasani.chatapp.databinding.ActivitySigninBinding
import com.vivekupasani.chatapp.ui.Activity.signup.getEmail


class SignIn : AppCompatActivity() {

    lateinit var binding: ActivitySigninBinding
    private val viewModel: Authentication by viewModels()

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setUpObservers()

        binding.btnContinue2.setOnClickListener {
            onLogInBtnClick()
        }

        binding.btnBack.setOnClickListener {
            onBackBtnClick()
        }

        binding.btnGoSignUp.setOnClickListener {
            onGoToSignUpBtnClick()
        }

    }

    private fun setUpObservers() {
        //runs when login is success
        viewModel.signInStatus.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        })

        //runs when login is failed
        viewModel.errorMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun onBackBtnClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun onGoToSignUpBtnClick() {
        binding.btnGoSignUp.setOnClickListener {
            startActivity(Intent(this, getEmail::class.java))
        }
    }

    private fun onLogInBtnClick() {
        val email = binding.userEmail.text.toString()
        val password = binding.userPassword.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        } else if (password.toString().isEmpty()) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.signInUser(email, password)
        }
    }

}