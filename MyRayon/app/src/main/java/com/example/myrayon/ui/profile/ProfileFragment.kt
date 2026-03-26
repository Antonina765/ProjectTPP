package com.example.myrayon.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.databinding.FragmentProfileBinding
import com.example.myrayon.model.Request
import com.example.myrayon.ui.adapters.RequestAdapter
import com.example.myrayon.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: RequestAdapter
    private val requestList = mutableListOf<Request>()
    private var currentUserId: Int = -1
    private var currentUserRole: String = "User"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())
        val prefs = requireContext().getSharedPreferences("MyRayonPrefs", 0)
        currentUserId = prefs.getInt("userId", -1)
        val user = dbHelper.getUser(currentUserId)
        currentUserRole = user?.role ?: "User"

        // Set greeting in header
        binding.tvGreeting.text = if (currentUserRole == "Admin") {
            "Администратор: ${user?.name ?: "Admin"}"
        } else {
            "Добро пожаловать, ${user?.name ?: "Пользователь"}!"
        }

        // Logout button - same for both roles
        binding.btnLogout.setOnClickListener {
            // Clear saved user data
            prefs.edit().clear().apply()
            // Go to login screen
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        if (currentUserRole == "Admin") {
            // Admin view: show all requests
            setupRecyclerView()
            loadRequests()
            binding.userInfoLayout.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            // Regular user view: show user info
            displayUserInfo(user)
            binding.userInfoLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    private fun displayUserInfo(user: com.example.myrayon.model.User?) {
        if (user == null) {
            Toast.makeText(requireContext(), "Ошибка загрузки пользователя", Toast.LENGTH_SHORT).show()
            return
        }
        binding.tvName.text = "Имя: ${user.name}"
        binding.tvEmail.text = "Email: ${user.email}"
        binding.tvAddress.text = "Адрес: ${user.address}"
        binding.tvRole.text = "Роль: ${user.role}"
    }

    private fun setupRecyclerView() {
        adapter = RequestAdapter(requestList, currentUserRole) { request ->
            // Admin can update status by clicking on a request
            if (currentUserRole == "Admin") {
                showStatusUpdateDialog(request)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadRequests() {
        requestList.clear()
        requestList.addAll(dbHelper.getAllRequests())
        adapter.notifyDataSetChanged()
    }

    private fun showStatusUpdateDialog(request: Request) {
        val statuses = arrayOf("New", "In process", "Done")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Изменить статус")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                dbHelper.updateRequestStatus(request.id, newStatus)
                loadRequests()
                Toast.makeText(requireContext(), "Статус обновлен", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}