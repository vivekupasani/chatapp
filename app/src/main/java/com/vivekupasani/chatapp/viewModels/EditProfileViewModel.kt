package com.vivekupasani.chatapp.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.vivekupasani.chatapp.models.Users

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private var token: String? = null

    private val _edited = MutableLiveData<Boolean>()
    val edited: LiveData<Boolean> = _edited

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun editProfile(imageUri: Uri?, username: String, email: String, password: String, about: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("Users").document(userId)

        // Fetch Firebase token for notifications
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result
                if (imageUri != null) {
                    // Upload new profile image if it exists
                    val storageRef = storage.getReference("Profile Pics").child("$userId.jpg")
                    storageRef.putFile(imageUri).addOnCompleteListener { uploadTask ->
                        if (uploadTask.isSuccessful) {
                            storageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                                if (downloadTask.isSuccessful) {
                                    val profilePicUrl = downloadTask.result.toString()

                                    // Create user with new profile image URL
                                    val updatedUser = Users(
                                        userId = userId,
                                        email = email,
                                        about = about,
                                        password = password,
                                        userName = username,
                                        profilePicURL = profilePicUrl,
                                        token = token ?: ""
                                    )
                                    updateUsersProfile(userRef, updatedUser)
                                } else {
                                    _error.value = "Failed to get profile image URL: ${downloadTask.exception?.message}"
                                }
                            }
                        } else {
                            _error.value = "Failed to upload profile image: ${uploadTask.exception?.message}"
                        }
                    }
                } else {
                    // Fetch the current profile picture if no new image is uploaded
                    userRef.get().addOnCompleteListener { documentTask ->
                        if (documentTask.isSuccessful) {
                            val document = documentTask.result
                            val currentProfilePicURL = document?.getString("profilePicURL").orEmpty()
                            val friendList = document?.get("friends") as? List<String> ?: emptyList()
                            val requestFriendList = document?.get("friendRequests") as? List<Map<String, Any>> ?: emptyList()


                            val updatedUser = Users(
                                userId = userId,
                                email = email,
                                about = about,
                                password = password,
                                userName = username,
                                profilePicURL = currentProfilePicURL,
                                token = token ?: "",
                                friends = friendList,
                                friendRequests = requestFriendList
                            )
                            updateUsersProfile(userRef, updatedUser)
                        } else {
                            _error.value = "Failed to fetch current profile: ${documentTask.exception?.message}"
                        }
                    }
                }
            } else {
                _error.value = "Failed to get FCM token: ${task.exception?.message}"
            }
        }
    }

    private fun updateUsersProfile(userRef: DocumentReference, user: Users) {
        userRef.set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _edited.value = true
            } else {
                _error.value = "Failed to update profile: ${task.exception?.message}"
            }
        }
    }
}
