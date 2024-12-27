package com.vivekupasani.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.EachUserInAddToChatsBinding
import com.vivekupasani.chatapp.models.Users

class NotificationAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<NotificationAdapter.viewHolder>() {

    var onAcceptBtnClick: ((Users) -> Unit)? = null

    class viewHolder(val binding: EachUserInAddToChatsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            EachUserInAddToChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentUser = userList[position]
        holder.apply {
            binding.username.text = "Accept request to chat"
            binding.titleforbtn.text = "Accept Request"
            binding.titleforBtn.text = "Accepted"
        }
        holder.apply {
            Glide.with(itemView).load(currentUser.profilePicURL)
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.profile)
            binding.fullname.text = currentUser.userName
        }

        holder.binding.AddToChat.setOnClickListener {
            holder.apply {
                binding.AddToChat.visibility = View.INVISIBLE
                binding.AddedToChat.visibility = View.VISIBLE
            }
            onAcceptBtnClick?.invoke(currentUser)
        }
    }

    fun updateList(newList: List<Users>) {
        userList.clear() // Clear the current list
        userList.addAll(newList) // Add all new items
        notifyDataSetChanged()
    }
}
