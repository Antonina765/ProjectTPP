package com.example.myrayon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myrayon.data.db.DBHelper

@Composable
fun RegisterScreen(db: DBHelper, onRegistered: (Int) -> Unit) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }

    val districts = db.getAllDistricts() // нужно будет добавить метод

    Column(Modifier.padding(16.dp)) {

        Text("Registration", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Выбор района
        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedTextField(
                value = district,
                onValueChange = {},
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                districts.forEach {
                    DropdownMenuItem(
                        text = { Text(it.name) },
                        onClick = {
                            district = it.name
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                db.addUser(name, email, address, "user")
                val user = db.getUserByEmail(email)
                if (user != null) onRegistered(user.id)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}
