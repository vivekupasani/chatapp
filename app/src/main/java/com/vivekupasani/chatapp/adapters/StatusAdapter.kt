package com.vivekupasani.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.EachStatusOfUserBinding
import com.vivekupasani.chatapp.models.status

class StatusAdapter : RecyclerView.Adapter<StatusAdapter.ViewHolder>() {

    private var statusList: ArrayList<status> = arrayListOf()
    var onStatusClick: ((status) -> Unit)? = null

    inner class ViewHolder(val binding: EachStatusOfUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentStatus: status) {
            // Load the profile picture
            Glide.with(itemView.context)
                .load(currentStatus.profilePicURL)
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.profileImage)

            // Load the status image
            if (!currentStatus.status.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(currentStatus.status)
                    .into(binding.mainStatus)
            } else {
                // Handle case where there is no status image
                binding.mainStatus.setImageResource(R.drawable.profile_placeholder) // Optional placeholder
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EachStatusOfUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < statusList.size) {
            val currentStatus = statusList[position]
            holder.bind(currentStatus)

            holder.itemView.setOnClickListener {
                onStatusClick?.invoke(currentStatus)
            }
        }
    }

    fun updateStatus(status: List<status>) {
        statusList.clear()
        statusList.addAll(status.filterNotNull()) // Filter out any null statuses if applicable
        notifyDataSetChanged()
    }
}
