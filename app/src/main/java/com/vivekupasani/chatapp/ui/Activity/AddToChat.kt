package com.vivekupasani.chatapp.ui.Activity

import AddToChatViewModel
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.mikelau.shimmerrecyclerviewx.ShimmerRecyclerViewX
import com.vivekupasani.chatapp.adapters.AddToChatAdapter
import com.vivekupasani.chatapp.databinding.ActivityAddToChatBinding
import com.vivekupasani.chatapp.viewModels.Friends

class AddToChat : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityAddToChatBinding
    private lateinit var adapter: AddToChatAdapter
    private lateinit var recyclerView: ShimmerRecyclerViewX
    private lateinit var refreshLayout: SwipeRefreshLayout

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel: AddToChatViewModel by viewModels()
    private val friendViewModel: Friends by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ensure that your app supports edge-to-edge layout
        binding = ActivityAddToChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SwipeRefreshLayout and RecyclerView
        refreshLayout = binding.swiperefresh
        refreshLayout.setOnRefreshListener(this)

        setUpRecyclerView()
        observeViewModel()
        observeFriendViewModel()

        recyclerView.showShimmerAdapter() // Show shimmer while loading data

        // Handle friend request button click
        adapter.onRequestBtnClick = { selectedUser ->
            friendViewModel.sendRequest(selectedUser)
        }

        // Handle back button click
        onBackBtnClick()
    }

    // Observe the FriendViewModel for friend request updates
    private fun observeFriendViewModel() {
        friendViewModel.sendRequest.observe(this, Observer {
//            Toast.makeText(this, "Friend Request Sent", Toast.LENGTH_SHORT).show()
        })
        friendViewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, "Failed to Send Request: $it", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Observe the AddToChatViewModel for user data
    private fun observeViewModel() {
        viewModel.userList.observe(this, Observer { users ->
            recyclerView.hideShimmerAdapter()

            if (users.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyList.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyList.visibility = View.GONE
                adapter.updateList(users)
            }
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            recyclerView.showShimmerAdapter() // Show shimmer if error occurs
        })
    }

    // Set up the RecyclerView with adapter
    private fun setUpRecyclerView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddToChatAdapter(arrayListOf())
        recyclerView.adapter = adapter
    }

    // Handle the back button click
    private fun onBackBtnClick() {
        binding.btnBack.setOnClickListener {
            finish() // Close the activity
        }
    }

    // Handle swipe to refresh
    override fun onRefresh() {
        viewModel.getUsers() // Refresh user data
        recyclerView.showShimmerAdapter() // Show shimmer while loading
        refreshLayout.isRefreshing = false // Stop the refresh animation
    }
}
