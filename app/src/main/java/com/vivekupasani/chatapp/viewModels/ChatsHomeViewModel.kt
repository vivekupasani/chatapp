package com.vivekupasani.chatapp.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.models.Users

class ChatsHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid

    private val _userList = MutableLiveData<List<Users>>()
    val userList: LiveData<List<Users>> = _userList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        getUsers()
    }

    fun getUsers() {
        currentUserId?.let { uid ->
            // Fetch the user data from Firestore asynchronously using addOnCompleteListener
            firestore.collection("Users").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successfully fetched the data
                        val querySnapshot = task.result
                        val users = querySnapshot?.toObjects(Users::class.java) ?: emptyList()

                        // Filter users: exclude the current user and include only friends
                        val filteredList = users.filter { user ->
                            user.userId != uid && user.friends.contains(uid)
                        }

                        // Post the filtered list to the LiveData
                        _userList.value = filteredList
                    } else {
                        // Handle failure in fetching users
                        Log.e("ChatsHomeViewModel", "Error fetching users", task.exception)
                        _error.value = "Error fetching users: ${task.exception?.message}"
                    }
                }
        } ?: run {
            // Handle the case where the user is not authenticated
            _error.value = "User not authenticated"
        }
    }
}
