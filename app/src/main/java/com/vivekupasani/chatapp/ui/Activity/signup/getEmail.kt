package com.vivekupasani.chatapp.ui.Activity.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.vivekupasani.chatapp.databinding.ActivityGetemailBinding
import com.vivekupasani.chatapp.ui.Activity.SignIn

class getEmail : AppCompatActivity() {

    lateinit var binding: ActivityGetemailBinding
    lateinit var userEmail: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGetemailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnGologin.setOnClickListener {
            onGoToLoginBtnClick()
        }

        binding.btnContinue1.setOnClickListener {
            checksForEmail()
        }

    }


    private fun checksForEmail() {
        userEmail = binding.emailAddress.text.trim().toString()

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.emailAddress.error = "Invalid Email"
        } else {
            val intent = Intent(this, getPassword::class.java)
            intent.putExtra("email", userEmail)
            startActivity(intent)
        }
    }

    private fun onGoToLoginBtnClick() {
        startActivity(Intent(this, SignIn::class.java))
    }


}
