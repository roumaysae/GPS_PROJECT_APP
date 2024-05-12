package com.example.myapplication

import GPSScreen
import HomeScreen
import LoginScreen
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginClick = { username, password ->
                                handleLogin(username, password, navController)
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(navController = navController, this@MainActivity) // Passer l'activité actuelle
                    }
                    composable("gps_screen/{imageUri}") { backStackEntry ->
                        val imageUri = Uri.parse(backStackEntry.arguments?.getString("imageUri"))
                        GPSScreen(imageUri)
                    }
                }
            }
        }
    }

    private fun handleLogin(username: String, password: String, navController: NavHostController) {
        // Vérifiez les informations d'identification, par exemple avec une API
        // Naviguer vers la page d'accueil si la connexion réussit
        navController.navigate("home")
    }
}
