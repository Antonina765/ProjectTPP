package com.example.myrayon.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrayon.R
import com.example.myrayon.model.Request

class RequestAdapter(
    private val requests: List<Request>,
    private val userRole: String,
    private val onStatusClick: (Request) -> Unit
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStreet: TextView = itemView.findViewById(R.id.tvStreet)
        val tvText: TextView = itemView.findViewById(R.id.tvText)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.tvStreet.text = "Street: ${request.street}"
        holder.tvText.text = request.text
        holder.tvStatus.text = "Status: ${request.status}"

        if (userRole == "Admin") {
            holder.itemView.setOnClickListener { onStatusClick(request) }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount() = requests.size
}