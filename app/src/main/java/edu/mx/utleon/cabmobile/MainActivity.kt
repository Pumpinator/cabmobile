// MainActivity.kt
package edu.mx.utleon.cabmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.mx.utleon.cabmobile.ui.components.Main
import edu.mx.utleon.cabmobile.ui.components.Camera
import edu.mx.utleon.cabmobile.ui.components.Navbar
import edu.mx.utleon.cabmobile.ui.components.Statistics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    val entry by navController.currentBackStackEntryAsState()
                    val route = entry?.destination?.route
                    if (route != "camera") {
                        Navbar(currentRoute = route ?: "main") { navController.navigate(it) }
                    }
                }
            ) { padding ->
                NavHost(
                    navController,
                    startDestination = "main",
                    modifier = Modifier.padding(padding)
                ) {
                    composable("main") { Main(navController) }
                    composable("camera") { Camera(navController) }
                    composable("statistics") { Statistics(navController) }
                }
            }
        }
    }
}