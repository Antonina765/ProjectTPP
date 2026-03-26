package com.example.myrayon.ui.votes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrayon.R
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.databinding.FragmentVotesBinding
import com.example.myrayon.model.Vote
import com.example.myrayon.ui.adapters.VoteAdapter

class VotesFragment : Fragment() {

    private var _binding: FragmentVotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: VoteAdapter
    private val voteList = mutableListOf<Vote>()
    private var currentUserRole: String = "User"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())
        val prefs = requireContext().getSharedPreferences("MyRayonPrefs", 0)
        val userId = prefs.getInt("userId", -1)
        currentUserRole = dbHelper.getUser(userId)?.role ?: "User"

        setupRecyclerView()
        loadVotes()

        if (currentUserRole == "Admin") {
            binding.fabAddVote.visibility = View.VISIBLE
            binding.fabAddVote.setOnClickListener {
                showAddVoteDialog()
            }
        } else {
            binding.fabAddVote.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = VoteAdapter(voteList) { vote, choice ->
            dbHelper.vote(vote.id, choice)
            loadVotes()
            Toast.makeText(requireContext(), "Added vote", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadVotes() {
        voteList.clear()
        voteList.addAll(dbHelper.getAllVotes())
        adapter.notifyDataSetChanged()
    }

    private fun showAddVoteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_vote, null)
        val etQuestion = view.findViewById<android.widget.EditText>(R.id.etQuestion)

        builder.setView(view)
            .setTitle("Create voting")
            .setPositiveButton("Create") { _, _ ->
                val question = etQuestion.text.toString()
                if (question.isNotBlank()) {
                    dbHelper.addVote(question)
                    loadVotes()
                    Toast.makeText(requireContext(), "Vote created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Add question", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cansel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}