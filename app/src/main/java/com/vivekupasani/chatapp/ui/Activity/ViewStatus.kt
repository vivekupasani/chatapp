package com.vivekupasani.chatapp.ui.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikelau.shimmerrecyclerviewx.ShimmerRecyclerViewX
import com.vivekupasani.chatapp.adapters.ViewStatusAdapter
import com.vivekupasani.chatapp.databinding.ActivityViewStatusBinding
import com.vivekupasani.chatapp.models.status
import com.vivekupasani.chatapp.viewModels.ViewStatusViewModel

class ViewStatus : AppCompatActivity() {

    private lateinit var binding: ActivityViewStatusBinding
    private lateinit var currentUserID: String
    private lateinit var recyclerViewX: ShimmerRecyclerViewX
    private lateinit var adapter: ViewStatusAdapter

    private val viewModel: ViewStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchDataFromIntent()
        setupRecyclerView()
        fetchStatusFromDb()
        observeViewModel()
        oncancelBtnClick()
        // Initially show shimmer effect and hide the cancel button
        binding.btnCancel.visibility = View.GONE
    }

    private fun oncancelBtnClick() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        recyclerViewX = binding.statusRecyclerView
        adapter = ViewStatusAdapter()
        recyclerViewX.layoutManager = LinearLayoutManager(this)
        recyclerViewX.adapter = adapter
    }

    private fun observeViewModel() {
        // Show shimmer effect while loading data
        recyclerViewX.showShimmerAdapter()
        binding.btnCancel.visibility = View.GONE

        viewModel.statusList.observe(this, Observer { statusList ->
            // Hide shimmer effect when data is fetched
            recyclerViewX.hideShimmerAdapter()
            binding.btnCancel.visibility = View.VISIBLE // Show cancel button when data is loaded
            Log.d("ViewStatusViewModel", "Fetched status: $statusList")

            if (statusList.isNullOrEmpty()) {
                // Show a message if no statuses are available
                Toast.makeText(this, "No statuses available", Toast.LENGTH_SHORT).show()
            } else {
                // Update the adapter with the fetched status list
                adapter.updateList(statusList as List<status>) // Correct type casting
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            // Hide shimmer effect and show cancel button if an error occurs
            recyclerViewX.hideShimmerAdapter()
            binding.btnCancel.visibility = View.VISIBLE
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun fetchStatusFromDb() {
        // Call ViewModel to fetch all statuses for the current user
        viewModel.showAllStatus(currentUserID)
    }

    private fun fetchDataFromIntent() {
        // Safely handle potential null value for user ID
        currentUserID = intent.getStringExtra("uID") ?: run {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if user ID is missing
            return
        }
    }
}
