package com.vivekupasani.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vivekupasani.chatapp.R
import com.vivekupasani.chatapp.databinding.EachUserViewStatusBinding
import com.vivekupasani.chatapp.models.status
import java.text.SimpleDateFormat
import java.util.Locale


class ViewStatusAdapter : RecyclerView.Adapter<ViewStatusAdapter.ViewHolder>() {

    private var statusList: List<status> = listOf()

    class ViewHolder(val binding: EachUserViewStatusBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            EachUserViewStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = statusList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentStatus = statusList[position]
        holder.apply {
            Glide.with(holder.itemView).load(currentStatus.status).into(binding.status)
            Glide.with(holder.itemView).load(currentStatus.profilePicURL)
                .placeholder(R.drawable.profile_placeholder).into(binding.profilePicture)
            binding.username.text = currentStatus.userName            // Set other views as needed
            binding.uploadTime.text =
                currentStatus.lastUpdated?.let { convertMillisToHourMinAMPM(it) } ?: "N/A"

        }
    }

    fun updateList(status: List<status>) {
        statusList = status
        notifyDataSetChanged()
    }

    private fun convertMillisToHourMinAMPM(milliseconds: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = java.util.Date(milliseconds)
        return sdf.format(date)
    }
}
