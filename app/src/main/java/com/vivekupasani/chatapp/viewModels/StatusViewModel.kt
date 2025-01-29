package com.vivekupasani.chatapp.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.vivekupasani.chatapp.models.Users
import com.vivekupasani.chatapp.models.status
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
class StatusViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _uploaded = MutableLiveData<Boolean>()
    val uploaded: LiveData<Boolean> get() = _uploaded

    private val _statusList = MutableLiveData<List<status>>()
    val statusList: LiveData<List<status>> get() = _statusList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        displayStatus()
        cleanupExpiredStatuses()
    }

    fun uploadStatus(image: Uri) {
        val currentUserId = auth.currentUser?.uid ?: run {
            _error.value = "User not logged in"
            return
        }

        // Fetch user details
        firestore.collection("Users").document(currentUserId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(Users::class.java)
                val currentUserName = user?.userName ?: "Unknown User"
                val currentProfilePic = user?.profilePicURL ?: ""

                // Upload image to Firebase Storage
                val storageRef = storage.getReference("Status")
                    .child("$currentUserId${System.currentTimeMillis()}.jpg")
                storageRef.putFile(image)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val newStatus = status(
                                currentUserName,
                                currentProfilePic,
                                currentUserId,
                                uri.toString(),
                                System.currentTimeMillis()
                            )

                            // Update user details in Firebase Realtime Database
                            val userDetails = mapOf(
                                "name" to currentUserName,
                                "profile" to currentProfilePic,
                                "userId" to currentUserId,
                                "lastUpdated" to System.currentTimeMillis()
                            )

                            database.getReference("Status").child(currentUserId)
                                .updateChildren(userDetails)
                                .addOnSuccessListener {
                                    database.getReference("Status").child(currentUserId)
                                        .child("Statuses").push()
                                        .setValue(newStatus)
                                        .addOnSuccessListener {
                                            _uploaded.value = true
                                        }
                                        .addOnFailureListener { e ->
                                            _error.value =
                                                "Error uploading status: ${e.localizedMessage}"
                                        }
                                }
                                .addOnFailureListener { e ->
                                    _error.value =
                                        "Error updating user status: ${e.localizedMessage}"
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Error uploading image: ${e.localizedMessage}"
                    }
            }
            .addOnFailureListener { e ->
                _error.value = "Error fetching user data: ${e.localizedMessage}"
            }
    }

    fun displayStatus() {
        val currentUserId = auth.currentUser?.uid ?: run {
            _error.value = "User not logged in"
            return
        }

        firestore.collection("Users").document(currentUserId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(Users::class.java)
                val friendsList = user?.friends ?: emptyList()

                val statusRef = database.getReference("Status")

                statusRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val allStatuses = mutableListOf<status>()

                        for (friendUserId in dataSnapshot.children) {
                            val statusesSnapshot = friendUserId.child("Statuses")

                            if (friendUserId.key == currentUserId || friendUserId.key in friendsList) {
                                for (statusSnapshot in statusesSnapshot.children) {
                                    val userStatus = statusSnapshot.getValue(status::class.java)
                                    userStatus?.let { statusObj ->
                                        allStatuses.add(statusObj)
                                    }
                                }
                            }
                        }

                        _statusList.postValue(allStatuses) // Ensuring LiveData updates UI
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _error.postValue("Error fetching statuses: ${databaseError.message}")
                    }
                })
            }
            .addOnFailureListener { e ->
                _error.postValue("Error fetching user data: ${e.localizedMessage}")
            }
    }

    fun cleanupExpiredStatuses() {
        val currentTime = System.currentTimeMillis()
        val expirationTime = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

        database.getReference("Status").get()
            .addOnSuccessListener { dataSnapshot ->
                for (snapshot in dataSnapshot.children) {
                    val statusesSnapshot = snapshot.child("Statuses")
                    for (statusSnapshot in statusesSnapshot.children) {
                        val statusObj = statusSnapshot.getValue(status::class.java)
                        statusObj?.let {
                            if (currentTime - it.lastUpdated > expirationTime) {
                                statusSnapshot.ref.removeValue()
                                    .addOnFailureListener { e ->
                                        _error.value =
                                            "Failed to remove expired status: ${e.localizedMessage}"
                                    }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                _error.value = "Failed to fetch statuses for cleanup: ${e.localizedMessage}"
            }
    }
}
