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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikelau.shimmerrecyclerviewx.ShimmerRecyclerViewX
import com.vivekupasani.chatapp.adapters.NotificationAdapter
import com.vivekupasani.chatapp.databinding.ActivityNotificationBinding
import com.vivekupasani.chatapp.viewModels.Friends
import com.vivekupasani.chatapp.viewModels.NotificationViewModel

class Notification : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var recyclerViewX: ShimmerRecyclerViewX
    private lateinit var adapter: NotificationAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout

    private val viewModel: Friends by viewModels()
    private val notiViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshLayout = binding.swiperefresh
        refreshLayout.setOnRefreshListener(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        recyclerViewX.showShimmerAdapter()
        observeViewModel()
        observeFriendsViewModel()
        onAcceptFriendBtnClick()
    }

    private fun observeViewModel() {
        notiViewModel.userList.observe(this, Observer { userList ->
            Log.d("NotificationActivity", "UserList updated: $userList")

            if (userList.isNullOrEmpty()) {
                // Hide RecyclerView and show Empty TextView
                binding.recyclerView.visibility = View.GONE
                binding.emptyList.visibility = View.VISIBLE
            } else {
                // Show RecyclerView and hide Empty TextView
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyList.visibility = View.GONE
                adapter.updateList(userList)
            }

            // Hide shimmer effect and stop refresh indicator
            recyclerViewX.hideShimmerAdapter()
            refreshLayout.isRefreshing = false
        })

        notiViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Log.e("NotificationActivity", "Error: $errorMessage")
            binding.emptyList.visibility = View.VISIBLE
            recyclerViewX.hideShimmerAdapter()
            refreshLayout.isRefreshing = false
        })
    }


    private fun onAcceptFriendBtnClick() {
        adapter.onAcceptBtnClick = { user ->
            viewModel.acceptRequest(user.userId)
        }
    }

    private fun observeFriendsViewModel() {
        viewModel.acceptedRequest.observe(this, Observer {
//            Toast.makeText(this, "Friend request accepted", Toast.LENGTH_SHORT).show()
            recyclerViewX.hideShimmerAdapter()
            refreshLayout.isRefreshing = false
        })

        viewModel.error.observe(this, Observer { error ->
            Log.e("NotificationActivity", "Error: $error")
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            recyclerViewX.hideShimmerAdapter()
            refreshLayout.isRefreshing = false
        })
    }

    private fun setupRecyclerView() {
        recyclerViewX = binding.recyclerView
        recyclerViewX.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(arrayListOf())
        recyclerViewX.adapter = adapter
    }

    override fun onRefresh() {
        recyclerViewX.showShimmerAdapter()
        notiViewModel.getNotifications()
    }
}
