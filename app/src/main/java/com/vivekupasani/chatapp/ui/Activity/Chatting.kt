package com.vivekupasani.chatapp.ui.Activity

import ChattingAdapter
import ChattingViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mikelau.shimmerrecyclerviewx.ShimmerRecyclerViewX
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.ActivityChattingBinding
import com.vivekupasani.chatapp.models.message
import com.vivekupasani.chatapp.ui.fragment.Profile

@Suppress("DEPRECATION")
class Chatting : AppCompatActivity() {

    private lateinit var binding: ActivityChattingBinding
    private var imageUrl: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ChattingAdapter
    private lateinit var recyclerView: ShimmerRecyclerViewX
    private lateinit var progressDialog: AlertDialog

    private lateinit var senderName: String

    var username: String = ""
    var token: String = ""
    var profilePic: String = ""
    var email: String = ""
    var about: String = ""
    var receiverUID: String = ""
    var senderId: String = ""
    var msg: String = ""

    private val viewModel: ChattingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        recyclerView = binding.chattingRV

        // Initialize the progress dialog
        initializingDialog()
        // Getting data from the intent
        fetchDataFromIntent()

        // Initialize ViewModel with sender and receiver IDs
        viewModel.initializeRooms(senderId, receiverUID)

        binding.username.setOnClickListener {
            gotoProfile()
        }

        binding.userProfile.setOnClickListener {
            gotoProfile()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSelectImage.setOnClickListener {
            onImageBtnClick()
        }

        binding.btnSend.setOnClickListener {
            onSendBtnClick()
        }

        // Setup RecyclerView
        setUpRecyclerView()

        // Start observing LiveData from the ViewModel
        observeDisplayChatViewModels()
        observeSendChatViewModel()

    }

    private fun gotoProfile() {
        val intent = Intent(this, OtherUserProfile::class.java)
        intent.apply {
            putExtra(Profile.Name, username)
            putExtra(Profile.profilePic, profilePic)
            putExtra(Profile.userId, receiverUID)
            putExtra(Profile.About, about)
            putExtra(Profile.Email, email)
        }
        startActivity(intent)
    }

    private fun onSendBtnClick() {
        msg = binding.messageBox.text.toString()

        if (msg.isNotBlank() || imageUrl != null) {
            if (imageUrl != null) {
                progressDialog.show()
            }
            viewModel.sendMessage(senderId, receiverUID, msg, imageUrl)
            imageUrl = null
            binding.messageBox.text.clear()

        } else {
            Toast.makeText(this, "Cannot send an empty message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchDataFromIntent() {
        username = intent.getStringExtra(Profile.Name) ?: ""
        token = intent.getStringExtra("token") ?: ""
        profilePic = intent.getStringExtra(Profile.profilePic) ?: ""
        receiverUID = intent.getStringExtra(Profile.userId) ?: ""
        about = intent.getStringExtra(Profile.About) ?: ""
        email = intent.getStringExtra(Profile.Email) ?: ""
        senderId = auth.currentUser?.uid ?: ""

        binding.username.text = username
        Glide.with(this).load(profilePic)
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.userProfile)
    }

    private fun setUpRecyclerView() {
        adapter = ChattingAdapter { imageUrl ->
            val intent = Intent(this, DisplayAttachment::class.java)
            intent.putExtra("imageUrl", imageUrl)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
        recyclerView.showShimmerAdapter()
    }

    private fun observeSendChatViewModel() {
        viewModel.msgSend.observe(this, Observer {
            binding.messageBox.text.clear()
            recyclerView.hideShimmerAdapter()
            progressDialog.dismiss()
        })

        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            recyclerView.hideShimmerAdapter()
            progressDialog.dismiss()
        })
    }

    private fun observeDisplayChatViewModels() {
        viewModel.messageList.observe(this, Observer { messages ->
            if (messages.isNullOrEmpty()) {
                binding.emptyList.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                binding.emptyList.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateList(messages as ArrayList<message>)
            }
            recyclerView.hideShimmerAdapter()
        })

        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            recyclerView.hideShimmerAdapter()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 45 && resultCode == RESULT_OK && data != null) {
            imageUrl = data.data
            progressDialog.show()
            viewModel.sendMessage(senderId, receiverUID, msg, imageUrl)
            imageUrl = null
        }
    }

    private fun onImageBtnClick() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 45)
    }

    private fun initializingDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sending_attachment, null)
        val builder = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setCancelable(false)
        }
        progressDialog = builder.create()
    }
}
