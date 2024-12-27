package com.vivekupasani.chatapp.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.adapters.ChatHomeAdapter
import com.vivekupasani.chatapp.databinding.FragmentChatsBinding
import com.vivekupasani.chatapp.models.Users
import com.vivekupasani.chatapp.ui.Activity.AddToChat
import com.vivekupasani.chatapp.ui.Activity.Chatting
import com.vivekupasani.chatapp.ui.Activity.Notification
import com.vivekupasani.chatapp.ui.Activity.OnBoard
import com.vivekupasani.chatapp.viewModels.ChatsHomeViewModel

class Chats : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var signOutDialog: Dialog
    private lateinit var recyclerViewX: RecyclerView
    private lateinit var adapter: ChatHomeAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private val viewModel: ChatsHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Initialize SignOut dialog
//        signOutDialog = Dialog(requireContext()).apply {
//            setContentView(R.layout.dialouge_box)
//            window?.setBackgroundDrawableResource(R.drawable.dialouge_box_background)
//        }
        binding.progressBar.visibility = View.VISIBLE
        // Set up swipe-to-refresh functionality
        refreshLayout = binding.swiperefresh
        refreshLayout.setOnRefreshListener {
            viewModel.getUsers()
            refreshLayout.isRefreshing = false // Stop refresh animation
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button click listeners
//        binding.btnSignOut.setOnClickListener { onSignOutBtnClick() }
        binding.btnNotification.setOnClickListener { onNotificationBtnClick() }
        binding.btnAddToChats.setOnClickListener { onAddToChatBtnClick() }

        // Set up RecyclerView and observe ViewModel
        setupRecyclerView()
        observeViewModel()
        onUserClick()
    }

    private fun onSignOutBtnClick() {
        val btnLogout = signOutDialog.findViewById<Button>(R.id.button_logout)
        val btnCancel = signOutDialog.findViewById<Button>(R.id.button_cancel)
        signOutDialog.show()
        signOutDialog.setCancelable(false)

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), OnBoard::class.java))
            activity?.finish()
        }

        btnCancel.setOnClickListener {
            signOutDialog.dismiss()
        }
    }

    private fun onNotificationBtnClick() {
        startActivity(Intent(context, Notification::class.java))
    }

    private fun onAddToChatBtnClick() {
        startActivity(Intent(requireContext(), AddToChat::class.java))
    }

    private fun setupRecyclerView() {
        recyclerViewX = binding.recyclerView
        adapter = ChatHomeAdapter(arrayListOf())
        recyclerViewX.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewX.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.userList.observe(viewLifecycleOwner, Observer { userList ->
            binding.progressBar.visibility = View.GONE // Hide ProgressBar when data is fetched
            if (userList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyList.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyList.visibility = View.GONE
                adapter.updateUser(userList as ArrayList<Users>)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE // Hide ProgressBar on error
        })
    }

    private fun onUserClick() {
        adapter.onUserClick = { user ->
            val intent = Intent(requireContext(), Chatting::class.java).apply {
                putExtra(Profile.Name, user.userName)
                putExtra("token", user.token)
                putExtra(Profile.profilePic, user.profilePicURL)
                putExtra(Profile.userId, user.userId)
                putExtra(Profile.About, user.about)
                putExtra(Profile.Email, user.email)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
