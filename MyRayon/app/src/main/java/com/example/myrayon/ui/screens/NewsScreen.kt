package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myrayon.data.db.DBHelper

@Composable
fun NewsScreen(db: DBHelper, isAdmin: Boolean) {
    val news = db.getAllNews()

    Column {
        if (isAdmin) {
            Button(onClick = { /* open add news dialog */ }) {
                Text("Add News")
            }
        }

        LazyColumn {
            items(news) { item ->
                Row {
                    Text(item.title)
                    if (isAdmin) {
                        IconButton(onClick = { db.deleteNews(item.id) }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }
        }
    }
}
