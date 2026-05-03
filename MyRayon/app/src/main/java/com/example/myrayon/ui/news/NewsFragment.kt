package com.example.myrayon.ui.news

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
import com.example.myrayon.databinding.FragmentNewsBinding
import com.example.myrayon.model.News
import com.example.myrayon.ui.adapters.NewsAdapter

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: NewsAdapter
    private val newsList = mutableListOf<News>()
    private var currentUserRole: String = "User"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DBHelper(requireContext())
        val prefs = requireContext().getSharedPreferences("MyRayonPrefs", 0)
        val userId = prefs.getInt("userId", -1)
        currentUserRole = dbHelper.getUser(userId)?.role ?: "User"

        setupRecyclerView()
        loadNews()

        if (currentUserRole == "Admin") {
            binding.fabAddNews.visibility = View.VISIBLE
            binding.fabAddNews.setOnClickListener {
                showAddNewsDialog()
            }
        } else {
            binding.fabAddNews.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(newsList) { news ->
            if (currentUserRole == "Admin") showNewsOptionsDialog(news)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    // НОВЫЙ МЕТОД
    private fun showNewsOptionsDialog(news: News) {
        val options = arrayOf("Редактировать", "Удалить")
        AlertDialog.Builder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditNewsDialog(news)
                    1 -> {
                        dbHelper.deleteNews(news.id)
                        loadNews()
                        Toast.makeText(requireContext(), "Новость удалена", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    // НОВЫЙ МЕТОД
    private fun showEditNewsDialog(news: News) {
        val view = layoutInflater.inflate(R.layout.dialog_add_news, null)
        val etTitle = view.findViewById<android.widget.EditText>(R.id.etTitle)
        val etContent = view.findViewById<android.widget.EditText>(R.id.etContent)

        etTitle.setText(news.title)
        etContent.setText(news.content)

        AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Редактировать новость")
            .setPositiveButton("Сохранить") { _, _ ->
                dbHelper.updateNews(news.id, etTitle.text.toString(), etContent.text.toString())
                loadNews()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun loadNews() {
        newsList.clear()
        newsList.addAll(dbHelper.getAllNews())
        adapter.notifyDataSetChanged()
    }

    private fun showAddNewsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_news, null)
        val etTitle = view.findViewById<android.widget.EditText>(R.id.etTitle)
        val etContent = view.findViewById<android.widget.EditText>(R.id.etContent)

        builder.setView(view)
            .setTitle("Добавить новость")
            .setPositiveButton("Добавить") { _, _ ->
                val title = etTitle.text.toString()
                val content = etContent.text.toString()
                if (title.isNotBlank() && content.isNotBlank()) {
                    dbHelper.addNews(title, content)
                    loadNews()
                    Toast.makeText(requireContext(), "Новость добавлена", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}