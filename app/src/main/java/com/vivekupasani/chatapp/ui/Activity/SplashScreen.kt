package com.vivekupasani.chatapp.ui.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.MainActivity
import com.vivekupasani.chatapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()



        setProgress(true)
        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser == null) {
                val intent = Intent(this, OnBoard::class.java)
                startActivity(intent)
                setProgress(false)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                setProgress(false)
                finish()
            }

        }, 1000)


    }


    fun setProgress(isProgress: Boolean) {
//        if (isProgress) {
//            binding.progressBar.isIndeterminate = true
//            binding.progressBar.visibility = android.view.View.VISIBLE
//        } else {
//            binding.progressBar.isIndeterminate = false
//            binding.progressBar.visibility = android.view.View.GONE
//        }
    }
}