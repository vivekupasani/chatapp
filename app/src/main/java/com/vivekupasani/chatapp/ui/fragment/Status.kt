package com.vivekupasani.chatapp.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.adapters.StatusAdapter
import com.vivekupasani.chatapp.databinding.FragmentStatusBinding
import com.vivekupasani.chatapp.ui.Activity.ViewStatus
import com.vivekupasani.chatapp.viewModels.StatusViewModel

class Status : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var progressDialog: AlertDialog
    private lateinit var adapter: StatusAdapter
    private val viewModel: StatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        initializeProgressDialog()

        binding.progressBar.visibility = View.VISIBLE

        // Swipe-to-refresh
        binding.swiperefresh.setOnRefreshListener {
            viewModel.displayStatus()
        }

        observeViewModel()

        binding.btnAddStatus.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 45)
        }

        // Fetch status if not already loaded
        viewModel.displayStatus()
    }

    private fun setupRecyclerView() {
        adapter = StatusAdapter()
        adapter.onStatusClick = { currentStatus ->
            val intent = Intent(requireContext(), ViewStatus::class.java).apply {
                putExtra("uName", currentStatus.userName)
                putExtra("uID", currentStatus.userId)
                putExtra("uProfile", currentStatus.profilePicURL)
            }
            startActivity(intent)
        }
        binding.statusRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@Status.adapter
        }
    }

    private fun initializeProgressDialog() {
        progressDialog = AlertDialog.Builder(requireContext())
            .setView(LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress, null))
            .setCancelable(false)
            .create()
        progressDialog.window?.setBackgroundDrawableResource(R.drawable.dialouge_box_background)
    }

    private fun observeViewModel() {
        viewModel.statusList.observe(viewLifecycleOwner, Observer { statusList ->
            binding.progressBar.visibility = View.GONE // Hide ProgressBar when data is fetched
            binding.swiperefresh.isRefreshing = false

            if (statusList.isNullOrEmpty()) {
                binding.statusRecyclerView.visibility = View.GONE
                binding.emptyList.visibility = View.VISIBLE
            } else {
                binding.statusRecyclerView.visibility = View.VISIBLE
                binding.emptyList.visibility = View.GONE
                adapter.updateStatus(statusList)
            }
        })

        viewModel.uploaded.observe(viewLifecycleOwner, Observer { uploaded ->
            if (uploaded) {
                Toast.makeText(requireContext(), "Status uploaded successfully", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            progressDialog.dismiss()
            binding.swiperefresh.isRefreshing = false
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 45 && resultCode == -1 && data != null) {
            imageUri = data.data ?: return
            progressDialog.show()
            viewModel.uploadStatus(imageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
