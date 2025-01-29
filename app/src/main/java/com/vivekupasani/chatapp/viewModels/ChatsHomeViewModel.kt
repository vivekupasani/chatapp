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
            firestore.collection("Users").addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("ChatsHomeViewModel", "Error fetching users", error)
                    _error.value = "Error fetching users: ${error.message}"
                    return@addSnapshotListener
                }

                val users = querySnapshot?.toObjects(Users::class.java) ?: emptyList()

                // Filter users: exclude the current user and include only friends
                val filteredList = users.filter { user ->
                    user.userId != uid && user.friends.contains(uid)
                }

                _userList.postValue(filteredList) // Update LiveData with real-time data
            }
        } ?: run {
            _error.value = "User not authenticated"
        }
    }

}
