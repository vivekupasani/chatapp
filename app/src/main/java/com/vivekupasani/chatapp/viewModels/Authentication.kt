import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.vivekupasani.chatapp.models.Users

class Authentication(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var token: String? = null

    private val _signupStatus = MutableLiveData<Boolean>()
    val signupStatus: LiveData<Boolean> = _signupStatus

    private val _signInStatus = MutableLiveData<Boolean>()
    val signInStatus: LiveData<Boolean> = _signInStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _emailSent = MutableLiveData<String>()
    val emailSend : LiveData<String> = _emailSent


    fun forgotPassword(email: String) {
        if (email.isEmpty()) {
            _errorMessage.postValue("Email is required.")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _emailSent.postValue("Password reset email sent. Check your inbox 📩")
                } else {
                    _errorMessage.postValue("Failed to send reset email: ${task.exception?.message}")
                }
            }
    }


    fun signUpUser(email: String, password: String) {
        // Sign up user asynchronously with a listener
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signupStatus.postValue(true)
                    storeDetailsInFirebase(email) // Store user details after successful sign-up
                } else {
                    _errorMessage.postValue(task.exception?.message)
                    _signupStatus.postValue(false)
                }
            }
    }

    fun signInUser(email: String, password: String) {
        // Sign in user asynchronously with a listener
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInStatus.postValue(true)
                } else {
                    _errorMessage.postValue(task.exception?.message)
                    _signInStatus.postValue(false)
                }
            }
    }

    private fun storeDetailsInFirebase(email: String) {
        // Get the FCM token asynchronously
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result // Successfully retrieved the FCM token
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Store user details in Firestore
                    val user = Users(
                        currentUser.uid, email, "", System.currentTimeMillis(),
                        token = token.toString(),
                    )
                    firestore.collection("Users")
                        .document(currentUser.uid)
                        .set(user)
                        .addOnCompleteListener { firestoreTask ->
                            if (!firestoreTask.isSuccessful) {
                                _errorMessage.postValue(firestoreTask.exception?.message)
                            }
                        }
                } else {
                    _errorMessage.postValue("User is not logged in.")
                }
            } else {
                _errorMessage.postValue(task.exception?.message)
            }
        }
    }
}
