package com.example.myrayon.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrayon.R
import com.example.myrayon.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUser: TextView = itemView.findViewById(R.id.tvUser)
        val tvText: TextView = itemView.findViewById(R.id.tvText)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = messages[position]
        holder.tvUser.text = msg.userName
        holder.tvText.text = msg.text
        holder.tvTime.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(Date(msg.timestamp))
    }

    override fun getItemCount() = messages.size
}