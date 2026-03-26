package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.model.User

@Composable
fun ProfileScreen(db: DBHelper, userId: Int) {
    val user = db.getUser(userId)

    Column(Modifier.padding(16.dp)) {
        Text("Name: ${user?.name}")
        Text("Email: ${user?.email}")
        Text("Address: ${user?.address}")

        if (user?.role == "admin") {
            AdminDistrictsScreen(db)
        }
    }
}
