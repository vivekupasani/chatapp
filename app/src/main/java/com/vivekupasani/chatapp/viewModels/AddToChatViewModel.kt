import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vivekupasani.chatapp.models.Users

class AddToChatViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid

    private val _userList = MutableLiveData<List<Users>>()
    val userList: LiveData<List<Users>> = _userList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        getUsers()
    }

    fun getUsers() {
        currentUserId?.let { uid ->
            // Fetch users from Firestore
            firestore.collection("Users").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Successfully fetched the data
                        val querySnapshot = task.result
                        val users = querySnapshot?.toObjects(Users::class.java) ?: emptyList()

                        // Filter the users to exclude the current user and those who are already friends
                        val filteredList = users.filter { user ->
                            user.userId != uid && !user.friends.contains(uid)
                        }

                        // Post the filtered list to LiveData
                        _userList.value = filteredList
                    } else {
                        // Handle failure in fetching users
                        _errorMessage.value = "Failed to load users: ${task.exception?.message}"
                    }
                }
        } ?: run {
            // Handle the case where the user is not authenticated
            _errorMessage.value = "User not authenticated"
        }
    }
}
