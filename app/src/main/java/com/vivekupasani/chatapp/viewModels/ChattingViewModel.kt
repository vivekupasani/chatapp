import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vivekupasani.chatapp.models.message

class ChattingViewModel(application: Application) : AndroidViewModel(application) {

    private var firebase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var senderRoom: String
    lateinit var receiverRoom: String

    private var _msgSend = MutableLiveData<Boolean>()
    val msgSend: LiveData<Boolean> = _msgSend

    private var _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _messageList = MutableLiveData<List<message>>()
    val messageList: LiveData<List<message>> = _messageList

    fun initializeRooms(senderId: String, receiverId: String) {
        senderRoom = senderId + receiverId
        receiverRoom = receiverId + senderId
        displayChats()
    }

    private fun displayChats() {
        // Fetch messages without coroutines, using addOnCompleteListener
        firebase.getReference("Chats")
            .child(senderRoom)
            .child("Messages")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val messages = mutableListOf<message>()
                    val snapshot = task.result
                    for (messageSnapshot in snapshot.children) {
                        val messageItem = messageSnapshot.getValue(message::class.java)
                        messageItem?.let { messages.add(it) }
                    }
                    _messageList.value = messages
                } else {
                    _error.value = "Error fetching messages: ${task.exception?.message}"
                }
            }
    }

    fun sendMessage(senderId: String, receiverId: String, messageText: String, imageUri: Uri?) {
        senderRoom = senderId + receiverId
        receiverRoom = receiverId + senderId

        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            if (imageUri != null) {
                val storageRef = storage.getReference("Attachments")
                    .child("$currentUserId-${System.currentTimeMillis()}.jpg")

                storageRef.putFile(imageUri)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            storageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                                if (downloadTask.isSuccessful) {
                                    val imageUrl = downloadTask.result.toString()
                                    //now store message on firebase
                                    sendMessageToFirebase(
                                        senderId,
                                        receiverId,
                                        messageText,
                                        imageUrl
                                    )
                                } else {
                                    _error.value =
                                        "Error getting image URL: ${downloadTask.exception?.message}"
                                }
                            }
                        } else {
                            _error.value = "Error uploading image: ${task.exception?.message}"
                        }
                    }
            } else {
                sendMessageToFirebase(senderId, receiverId, messageText, "")
            }
        } else {
            _error.value = "User not authenticated"
        }
    }

    private fun sendMessageToFirebase(
        senderId: String,
        receiverId: String,
        messageText: String,
        imageUrl: String
    ) {
        val messageModel = message(messageText, senderId, imageUrl, System.currentTimeMillis())

        // Push the message to both sender and receiver rooms in Firebase
        firebase.getReference("Chats")
            .child(senderRoom)
            .child("Messages")
            .push()
            .setValue(messageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebase.getReference("Chats")
                        .child(receiverRoom)
                        .child("Messages")
                        .push()
                        .setValue(messageModel)
                        .addOnCompleteListener { receiverTask ->
                            if (receiverTask.isSuccessful) {
                                // Update the message list immediately
                                val currentMessages =
                                    _messageList.value?.toMutableList() ?: mutableListOf()
                                currentMessages.add(messageModel)
                                _messageList.value = currentMessages

                                _msgSend.value = true
                            } else {
                                _error.value =
                                    "Error sending message to receiver: ${receiverTask.exception?.message}"
                            }
                        }
                } else {
                    _error.value = "Error sending message to sender: ${task.exception?.message}"
                }
            }
    }
}
