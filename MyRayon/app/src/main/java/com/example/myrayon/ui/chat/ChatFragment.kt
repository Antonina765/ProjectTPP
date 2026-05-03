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
    private var currentUserRole: String = "User"
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
        currentUserRole = dbHelper.getUser(currentUserId)?.role ?: "User"

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

    // НОВЫЙ МЕТОД
    private fun showMessageOptionsDialog(message: Message) {
        val options = mutableListOf<String>()
        if (message.userId == currentUserId) {
            options.add("Редактировать")
            options.add("Удалить")
        } else if (currentUserRole == "Admin") {
            options.add("Удалить")
        }

        if (options.isEmpty()) return

        android.app.AlertDialog.Builder(requireContext())
            .setItems(options.toTypedArray()) { _, which ->
                when (options[which]) {
                    "Удалить" -> {
                        dbHelper.deleteMessage(message.id)
                        loadMessages()
                    }
                    "Редактировать" -> showEditMessageDialog(message)
                }
            }.show()
    }

    // НОВЫЙ МЕТОД
    private fun showEditMessageDialog(message: Message) {
        val editText = android.widget.EditText(requireContext())
        editText.setText(message.text)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Редактировать сообщение")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newText = editText.text.toString()
                if (newText.isNotBlank()) {
                    dbHelper.updateMessage(message.id, newText)
                    loadMessages()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(messageList) { message ->
            showMessageOptionsDialog(message)
        }
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