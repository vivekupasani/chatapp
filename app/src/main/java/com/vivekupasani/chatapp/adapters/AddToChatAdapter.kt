package com.vivekupasani.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.EachUserInAddToChatsBinding
import com.vivekupasani.chatapp.models.Users

class AddToChatAdapter(var userList: ArrayList<Users>) :
    RecyclerView.Adapter<AddToChatAdapter.viewHolder>() {

    var onRequestBtnClick: ((Users) -> Unit)? = null

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
            Glide.with(itemView).load(currentUser.profilePicURL)
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.profile)
            binding.fullname.text = currentUser.userName
        }

        holder.binding.AddToChat.setOnClickListener {
            holder.binding.AddToChat.visibility = View.INVISIBLE
            holder.binding.AddedToChat.visibility = View.VISIBLE
            onRequestBtnClick!!.invoke(currentUser)
        }
    }

    fun updateList(newList: List<Users>) {
        userList = newList as ArrayList<Users>
        notifyDataSetChanged()
    }
}