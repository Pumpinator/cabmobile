// MainActivity.kt
package edu.mx.utleon.cabmobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.mx.utleon.cabmobile.ui.components.Main
import edu.mx.utleon.cabmobile.ui.components.Camera
import edu.mx.utleon.cabmobile.ui.components.Navbar
import edu.mx.utleon.cabmobile.ui.components.Statistics

class MainActivity : ComponentActivity() {

    private var hasCameraPermission by mutableStateOf(false)
    private var hasLocationPermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: false
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndRequestPermissions()

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

    private fun checkAndRequestPermissions() {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if (!hasCameraPermission) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (!hasLocationPermission) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    fun canUseCamera(): Boolean = hasCameraPermission

    fun canUseLocation(): Boolean = hasLocationPermission

    fun requestCameraPermission() {
        if (!hasCameraPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    fun requestLocationPermission() {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }
}