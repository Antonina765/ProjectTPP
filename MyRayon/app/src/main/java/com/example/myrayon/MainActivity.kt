package com.example.myrayon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.myrayon.data.db.DBHelper
import com.example.myrayon.ui.screens.*

class MainActivity : ComponentActivity() {

    lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DBHelper(this)

        if (dbHelper.getUser(1) == null) {
            dbHelper.addUser("Super Admin", "admin@domain.com", "Admin Address", "admin")
        }

        setContent {
            MainScreen(dbHelper)
        }
    }
}

@Composable
fun MainScreen(dbHelper: DBHelper) {
    var currentUserId by remember { mutableStateOf(0) }
    val navController = rememberNavController()

    val tabs = listOf(
        BottomNavItem("News", Icons.Default.List, "news"),
        BottomNavItem("Profile", Icons.Default.Person, "profile"),
        BottomNavItem("Requests", Icons.Default.Build, "requests"),
        BottomNavItem("Chat", Icons.Default.Email, "chat"),
        BottomNavItem("Voting", Icons.Default.Notifications, "voting")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = navController.currentDestination?.route == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "news",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("news") {
                NewsScreen(dbHelper, isAdmin = (currentUserId == 1))
            }
            composable("profile") {
                if (currentUserId == 0) {
                    LoginScreen(dbHelper) { userId ->
                        currentUserId = userId
                        navController.navigate("profile") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                } else {
                    ProfileScreen(dbHelper, currentUserId)
                }
            }
            composable("requests") {
                if (currentUserId != 0) {
                    RequestsScreen(dbHelper, currentUserId)
                } else {
                    NotAuthorizedScreen("requests")
                }
            }
            composable("chat") {
                if (currentUserId != 0) {
                    ChatScreen(dbHelper, currentUserId)
                } else {
                    NotAuthorizedScreen("chat")
                }
            }
            composable("voting") {
                if (currentUserId != 0) {
                    VotingScreen(dbHelper, isAdmin = (currentUserId == 1))
                } else {
                    NotAuthorizedScreen("voting")
                }
            }
        }
    }
}

data class BottomNavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)

@Composable
fun NotAuthorizedScreen(feature: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("You should enter or login  \"$feature\".", style = MaterialTheme.typography.bodyLarge)
    }
}