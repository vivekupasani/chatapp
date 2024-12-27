package com.vivekupasani.chatapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.models.Users

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userList = MutableLiveData<List<Users>>()
    val userList: LiveData<List<Users>> = _userList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        getNotifications()
    }

    fun getNotifications() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val friendRequests =
                            document.get("friendRequests") as? List<Map<String, Any>>
                        if (friendRequests != null && friendRequests.isNotEmpty()) {
                            val users = mutableListOf<Users>()
                            for (request in friendRequests) {
                                val friendId = request["fromUid"] as? String
                                if (friendId != null) {
                                    firestore.collection("Users").document(friendId).get()
                                        .addOnSuccessListener { friendDoc ->
                                            val user = friendDoc.toObject(Users::class.java)
                                            if (user != null) {
                                                users.add(user)
                                            }
                                            // Update the user list after every request is processed
                                            _userList.value = users.toList()
                                        }
                                        .addOnFailureListener { e ->
                                            _errorMessage.value =
                                                "Error fetching friend data: ${e.message}"
                                        }
                                }
                            }
                        } else {
                            _errorMessage.value = "No friend requests found."
                        }
                    } else {
                        _errorMessage.value = "User document not found."
                    }
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = "Error fetching user data: ${e.message}"
                }
        } else {
            _errorMessage.value = "User not logged in."
        }
    }
}
