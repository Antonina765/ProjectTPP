package com.example.myrayon.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelper(this)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val address = binding.etAddress.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank() || address.isBlank()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email already exists
            val existing = dbHelper.getUserByEmail(email)
            if (existing != null) {
                Toast.makeText(this, "User with this email already exist", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dbHelper.addUser(name, email, password, address)
            Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}