import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.ChatReceiverDesignBinding
import com.vivekupasani.chatapp.databinding.ChatSenderDesignBinding
import com.vivekupasani.chatapp.models.message


class ChattingAdapter(private val onAttachmentClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messageList = ArrayList<message>()
    private val SENDITEM = 1
    private val RECEIVEITEM = 2
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    // ViewHolder for sender messages
    class SenderViewHolder(var binding: ChatSenderDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: message, onAttachmentClick: (String) -> Unit) {
            if (message.imageUrl.isNullOrEmpty()) {
                binding.chatMessage.visibility = View.VISIBLE
                binding.imageMessage.visibility = View.GONE
                binding.chatMessage.text = message.messageText
            } else {
                binding.chatMessage.visibility = View.GONE
                binding.imageMessage.visibility = View.VISIBLE
                Glide.with(itemView).load(message.imageUrl)
                    .placeholder(R.drawable.dialouge_box_background)
                    .into(binding.imageMessage)

                binding.imageMessage.setOnClickListener {
                    onAttachmentClick(message.imageUrl!!) // Send image URL to activity
                }
            }
        }
    }

    // ViewHolder for receiver messages
    class ReceiverViewHolder(var binding: ChatReceiverDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: message, onAttachmentClick: (String) -> Unit) {
            if (message.imageUrl.isNullOrEmpty()) {
                binding.chatMessage.visibility = View.VISIBLE
                binding.imageMessage.visibility = View.GONE
                binding.chatMessage.text = message.messageText
            } else {
                binding.chatMessage.visibility = View.GONE
                binding.imageMessage.visibility = View.VISIBLE
                Glide.with(itemView).load(message.imageUrl)
                    .placeholder(R.drawable.dialouge_box_background)
                    .into(binding.imageMessage)

                binding.imageMessage.setOnClickListener {
                    onAttachmentClick(message.imageUrl!!) // Send image URL to activity
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENDITEM) {
            val binding = ChatSenderDesignBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            SenderViewHolder(binding)
        } else {
            val binding = ChatReceiverDesignBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ReceiverViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == auth.currentUser?.uid) SENDITEM else RECEIVEITEM
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if (holder is SenderViewHolder) {
            holder.bind(message, onAttachmentClick)
        } else if (holder is ReceiverViewHolder) {
            holder.bind(message, onAttachmentClick)
        }
    }

    fun updateList(newMessages: ArrayList<message>) {
        messageList.clear()
        messageList.addAll(newMessages)
        notifyDataSetChanged()
    }
}
