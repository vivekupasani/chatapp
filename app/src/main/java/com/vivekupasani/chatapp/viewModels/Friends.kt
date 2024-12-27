package com.vivekupasani.chatapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.models.Users

class Friends(application: Application) : AndroidViewModel(application) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _sendRequest = MutableLiveData<Boolean>()
    val sendRequest: LiveData<Boolean> = _sendRequest

    private val _acceptedRequest = MutableLiveData<Boolean>()
    val acceptedRequest: LiveData<Boolean> = _acceptedRequest

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Function to send a friend request to the selected user
    fun sendRequest(selectedUser: Users) {
        val currentUserId = auth.currentUser?.uid ?: return
        val currentUserRef = firestore.collection("Users").document(currentUserId)
        val recipientUserRef = firestore.collection("Users").document(selectedUser.userId)

        // Fetch current user data
        currentUserRef.get().addOnCompleteListener { documentTask ->
            if (documentTask.isSuccessful) {
                val documentSnapshot = documentTask.result
                val currentUserName = documentSnapshot?.getString("userName") ?: "Unknown"
                val currentUserProfilePic = documentSnapshot?.getString("profilePicURL") ?: ""
                val currentUserAbout = documentSnapshot?.getString("about") ?: ""

                val friendRequest = hashMapOf(
                    "fromUid" to currentUserId,
                    "fromName" to currentUserName,
                    "fromProfilePicture" to currentUserProfilePic,
                    "fromAbout" to currentUserAbout,
                    "status" to "pending"
                )

                // Update the recipient's friend requests
                recipientUserRef.update("friendRequests", FieldValue.arrayUnion(friendRequest))
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            _sendRequest.value = true
                        } else {
                            _error.value =
                                "Error sending friend request: ${updateTask.exception?.message}"
                        }
                    }
            } else {
                _error.value =
                    "Error fetching current user data: ${documentTask.exception?.message}"
            }
        }
    }

    // Function to accept a friend request from the selected user
    fun acceptRequest(selectedUserId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val currentUserRef = firestore.collection("Users").document(currentUserId)
        val recipientUserRef = firestore.collection("Users").document(selectedUserId)

        // Retrieve the friend request details from the recipient user
        recipientUserRef.get().addOnCompleteListener { documentTask ->
            if (documentTask.isSuccessful) {
                val documentSnapshot = documentTask.result
                val friendRequests =
                    documentSnapshot?.get("friendRequests") as? MutableList<Map<String, Any>>

                if (friendRequests != null) {
                    // Filter out the request to be removed
                    val updatedRequests = friendRequests.filter { request ->
                        request["fromUid"] != selectedUserId
                    }

                    // Update the recipient's friendRequests array with the filtered list
                    currentUserRef.update("friendRequests", updatedRequests)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Add both users to each other's friends list
                                currentUserRef.update(
                                    "friends",
                                    FieldValue.arrayUnion(selectedUserId)
                                )
                                    .addOnCompleteListener { addFriendTask ->
                                        if (addFriendTask.isSuccessful) {
                                            recipientUserRef.update(
                                                "friends",
                                                FieldValue.arrayUnion(currentUserId)
                                            )
                                                .addOnCompleteListener { recipientAddFriendTask ->
                                                    if (recipientAddFriendTask.isSuccessful) {
                                                        _acceptedRequest.value = true
                                                    } else {
                                                        _error.value =
                                                            "Error adding friend to recipient's list: ${recipientAddFriendTask.exception?.message}"
                                                    }
                                                }
                                        } else {
                                            _error.value =
                                                "Error adding friend to current user's list: ${addFriendTask.exception?.message}"
                                        }
                                    }
                            } else {
                                _error.value =
                                    "Error updating friend requests: ${updateTask.exception?.message}"
                            }
                        }
                } else {
                    _error.value = "No friend requests found."
                }
            } else {
                _error.value =
                    "Error retrieving friend requests: ${documentTask.exception?.message}"
            }
        }
    }
}
