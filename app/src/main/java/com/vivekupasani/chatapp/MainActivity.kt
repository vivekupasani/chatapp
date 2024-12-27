package com.vivekupasani.chatapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.vivekupasani.chatapp.databinding.ActivityMainBinding
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController
        navController = findNavController(R.id.fragmentContainerView)

        // Setup SmoothBottomBar
        setupSmoothBottomMenu()

        window.statusBarColor = Color.TRANSPARENT
    }

    private fun setupSmoothBottomMenu() {
        // Map positions to navigation graph destination IDs
        val destinations = mapOf(
            0 to R.id.chats,   // Position 0 maps to the "Chats" fragment
            1 to R.id.status,  // Position 1 maps to the "Status" fragment
            2 to R.id.profile  // Position 2 maps to the "Profile" fragment
        )

        // Handle item selection in SmoothBottomBar
        binding.bottomBar.onItemSelected = { position ->
            destinations[position]?.let { destinationId ->
                if (navController.currentDestination?.id != destinationId) {
                    navController.navigate(destinationId)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
