package com.vivekupasani.chatapp.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.models.Users

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<Users>()
    val user: LiveData<Users> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(Users::class.java)
                        _user.value = userData!!
                    } else {
                        _error.value = "User data not found"
                    }
                }
                .addOnFailureListener { exception ->
                    _error.value = "Error fetching user data: ${exception.message}"
                }
        } else {
            _error.value = "User not logged in"
        }
    }
}
