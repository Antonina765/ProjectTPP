package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myrayon.data.db.DBHelper

@Composable
fun ChatScreen(dbHelper: DBHelper, CurrentUserId: Int) {
    var text by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(dbHelper.getAllMessages()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Home chat", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth().height(50.dp))
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (text.isNotBlank()) {
                dbHelper.addMessage(1, text)
                messages = dbHelper.getAllMessages()
                text = ""
            }
        }) {
            Text("Send")
        }

        Spacer(modifier = Modifier.height(16.dp))
        messages.forEach { msg ->
            Text("${msg.userId}: ${msg.text}")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}