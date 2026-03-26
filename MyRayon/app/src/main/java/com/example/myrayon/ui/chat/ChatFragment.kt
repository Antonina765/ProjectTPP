package com.example.myrayon.ui.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.databinding.FragmentChatBinding
import com.example.myrayon.model.Message
import com.example.myrayon.ui.adapters.MessageAdapter

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: MessageAdapter
    private val messageList = mutableListOf<Message>()
    private var currentUserId: Int = -1

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var refreshRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())
        val prefs = requireContext().getSharedPreferences("MyRayonPrefs", 0)
        currentUserId = prefs.getInt("userId", -1)

        setupRecyclerView()
        loadMessages()

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            if (text.isNotBlank()) {
                dbHelper.addMessage(currentUserId, text)
                binding.etMessage.text.clear()
                loadMessages()
            } else {
                Toast.makeText(requireContext(), "Enter message", Toast.LENGTH_SHORT).show()
            }
        }

        // Auto-refresh every 5 seconds
        refreshRunnable = Runnable {
            loadMessages()
            handler.postDelayed(refreshRunnable, 5000)
        }
        handler.post(refreshRunnable)
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(messageList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadMessages() {
        messageList.clear()
        messageList.addAll(dbHelper.getAllMessagesWithUsers())
        adapter.notifyDataSetChanged()
        binding.recyclerView.scrollToPosition(messageList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable)
        _binding = null
    }
}