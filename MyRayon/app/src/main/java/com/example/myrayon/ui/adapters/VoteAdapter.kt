package com.example.myrayon.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myrayon.R
import com.example.myrayon.model.Vote

class VoteAdapter(
    private val votes: List<Vote>,
    private val onVoteClick: (Vote, String) -> Unit
) : RecyclerView.Adapter<VoteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val tvAgree: TextView = itemView.findViewById(R.id.tvAgree)
        val tvDisagree: TextView = itemView.findViewById(R.id.tvDisagree)
        val tvAbstain: TextView = itemView.findViewById(R.id.tvAbstain)
        val btnAgree: Button = itemView.findViewById(R.id.btnAgree)
        val btnDisagree: Button = itemView.findViewById(R.id.btnDisagree)
        val btnAbstain: Button = itemView.findViewById(R.id.btnAbstain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vote, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vote = votes[position]
        holder.tvQuestion.text = vote.question
        holder.tvAgree.text = "За: ${vote.agree}"
        holder.tvDisagree.text = "Против: ${vote.disagree}"
        holder.tvAbstain.text = "Воздержались: ${vote.abstain}"

        holder.btnAgree.setOnClickListener { onVoteClick(vote, "agree") }
        holder.btnDisagree.setOnClickListener { onVoteClick(vote, "disagree") }
        holder.btnAbstain.setOnClickListener { onVoteClick(vote, "abstain") }
    }

    override fun getItemCount() = votes.size
}