package com.example.myrayon.ui.requests

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrayon.R
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.databinding.FragmentRequestsBinding
import com.example.myrayon.model.Request
import com.example.myrayon.ui.adapters.RequestAdapter

class RequestsFragment : Fragment() {

    private var _binding: FragmentRequestsBinding? = null
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
        _binding = FragmentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())
        val prefs = requireContext().getSharedPreferences("MyRayonPrefs", 0)
        currentUserId = prefs.getInt("userId", -1)
        currentUserRole = dbHelper.getUser(currentUserId)?.role ?: "User"

        setupRecyclerView()
        loadRequests()

        binding.fabAddRequest.setOnClickListener {
            showAddRequestDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = RequestAdapter(requestList, currentUserRole) { request, action ->
            if (currentUserRole == "Admin") {
                when (action) {
                    "status" -> showStatusUpdateDialog(request)
                    "options" -> showRequestOptionsDialog(request)
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    // НОВЫЙ МЕТОД
    private fun showRequestOptionsDialog(request: Request) {
        val options = arrayOf("Редактировать", "Удалить")
        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditRequestDialog(request)
                    1 -> {
                        dbHelper.deleteRequest(request.id)
                        loadRequests()
                        Toast.makeText(requireContext(), "Заявка удалена", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    // НОВЫЙ МЕТОД
    private fun showEditRequestDialog(request: Request) {
        val view = layoutInflater.inflate(R.layout.dialog_add_request, null)
        val etStreet = view.findViewById<android.widget.EditText>(R.id.etStreet)
        val etText = view.findViewById<android.widget.EditText>(R.id.etText)

        etStreet.setText(request.street)
        etText.setText(request.text)

        AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Редактировать заявку")
            .setPositiveButton("Сохранить") { _, _ ->
                dbHelper.updateRequestInfo(request.id, etStreet.text.toString(), etText.text.toString())
                loadRequests()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun loadRequests() {
        requestList.clear()
        requestList.addAll(dbHelper.getAllRequests())
        adapter.notifyDataSetChanged()
    }

    private fun showAddRequestDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_request, null)
        val etStreet = view.findViewById<android.widget.EditText>(R.id.etStreet)
        val etText = view.findViewById<android.widget.EditText>(R.id.etText)

        builder.setView(view)
            .setTitle("Add request")
            .setPositiveButton("Send") { _, _ ->
                val street = etStreet.text.toString()
                val text = etText.text.toString()
                if (street.isNotBlank() && text.isNotBlank()) {
                    dbHelper.addRequest(currentUserId, street, text)
                    loadRequests()
                    Toast.makeText(requireContext(), "Request Added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cansel", null)
            .show()
    }

    private fun showStatusUpdateDialog(request: Request) {
        val statuses = arrayOf("New", "In process", "Done", "Denied")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change status")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                dbHelper.updateRequestStatus(request.id, newStatus)
                loadRequests()
                Toast.makeText(requireContext(), "Status updated", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}