package com.vivekupasani.chatapp.ui.Activity

import Authentication
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.vivekupasani.chatapp.databinding.ActivityForgotpasswordBinding

class forgotPassword : AppCompatActivity() {

    lateinit var binding : ActivityForgotpasswordBinding
    private val viewModel: Authentication by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpObservers()

        binding.sendEmail.setOnClickListener {
            val email = binding.emailAddress.text.toString().trim()
            sendEmail(email)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpObservers() {
        viewModel.emailSend.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
            finishAffinity()
        })

        viewModel.errorMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun sendEmail(email: String) {
        if (email.isNotEmpty()) {
            viewModel.forgotPassword(email)
        } else {
            Toast.makeText(this, "Enter an email", Toast.LENGTH_SHORT).show()
        }
    }
}