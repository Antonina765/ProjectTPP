package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myrayon.data.db.DBHelper

@Composable
fun RequestsScreen(dbHelper: DBHelper, CurrentUserId: Int) {
    var text by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf(dbHelper.getAllRequests()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Vote on repair", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth().height(50.dp))
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (text.isNotBlank()) {
                dbHelper.addRequest(1, text)
                requests = dbHelper.getAllRequests()
                text = ""
            }
        }) {
            Text("Add vote")
        }

        Spacer(modifier = Modifier.height(16.dp))

        requests.forEach { r ->
            Text("${r.id}: ${r.text} [${r.status}]")
            Row {
                Button(onClick = { dbHelper.updateRequestStatus(r.id, "In progress"); requests = dbHelper.getAllRequests() }) {
                    Text("In progress")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { dbHelper.updateRequestStatus(r.id, "Done"); requests = dbHelper.getAllRequests() }) {
                    Text("Done")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}