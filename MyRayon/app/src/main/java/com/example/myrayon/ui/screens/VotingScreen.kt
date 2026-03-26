package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myrayon.data.db.DBHelper

@Composable
fun VotingScreen(dbHelper: DBHelper, isAdmin: Boolean) {
    var question by remember { mutableStateOf("") }
    var votes by remember { mutableStateOf(dbHelper.getAllVotes()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Voting", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(value = question, onValueChange = { question = it }, modifier = Modifier.fillMaxWidth().height(50.dp))
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (question.isNotBlank()) {
                dbHelper.addVote(question)
                votes = dbHelper.getAllVotes()
                question = ""
            }
        }) {
            Text("Create new Voting")
        }

        Spacer(modifier = Modifier.height(16.dp))
        votes.forEach { v ->
            Text("${v.id}: ${v.question}")
            Row {
                Button(onClick = { dbHelper.vote(v.id, "agree"); votes = dbHelper.getAllVotes() }) { Text("agree (${v.yes})") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { dbHelper.vote(v.id, "disagree"); votes = dbHelper.getAllVotes() }) { Text("disagree (${v.no})") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { dbHelper.vote(v.id, "abstain"); votes = dbHelper.getAllVotes() }) { Text("abstain (${v.abstain})") }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}