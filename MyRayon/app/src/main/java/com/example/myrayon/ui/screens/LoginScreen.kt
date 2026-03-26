package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myrayon.data.db.DBHelper

@Composable
fun LoginScreen(db: DBHelper, onLogin: (Int) -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val user = db.getUserByEmail(email)
                if (user != null) {
                    onLogin(user.id)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onRegister) {
            Text("Register")
        }
    }
}
