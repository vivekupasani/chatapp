package com.vivekupasani.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.EachUserInChatsBinding
import com.vivekupasani.chatapp.models.Users
import java.text.SimpleDateFormat
import java.util.Locale

class ChatHomeAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<ChatHomeAdapter.viewHolder>() {

    var onUserClick: ((Users) -> Unit)? = null

    class viewHolder(val binding: EachUserInChatsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            EachUserInChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentUser = userList[position]

        holder.apply {
            Glide.with(itemView).load(currentUser.profilePicURL).placeholder(R.drawable.profile_placeholder).into(binding.profile)
            binding.apply {
                username.text = currentUser.userName

                // Check if lastMessage is null or empty
                lastMsg.text = currentUser.about

                // Convert timestamp to "hh:mm a" format
                msgTime.text = currentUser.timestamp?.let { convertMillisToHourMinAMPM(it) } ?: "N/A"
            }

            itemView.setOnClickListener {
                onUserClick?.invoke(currentUser)
            }
        }
    }

    fun updateUser(newList: ArrayList<Users>) {
        userList = newList
        notifyDataSetChanged()
    }

    // Function to convert milliseconds to "hh:mm a" format
    private fun convertMillisToHourMinAMPM(milliseconds: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = java.util.Date(milliseconds)
        return sdf.format(date)
    }
}
