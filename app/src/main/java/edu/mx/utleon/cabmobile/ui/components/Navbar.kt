// app/src/main/java/edu/mx/utleon/cabmobile/ui/components/BottomNavBar.kt
package edu.mx.utleon.cabmobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import edu.mx.utleon.cabmobile.R
import edu.mx.utleon.cabmobile.ui.theme.ColorPrimary

sealed class NavItem(
    val route: String,
    val iconVector: ImageVector? = null,
    val iconRes: Int? = null,
    val label: String
) {
    object Camera : NavItem("camera", iconRes = R.drawable.camera, label = "Cámara")
    object Home : NavItem("main", iconVector = Icons.Filled.Home, label = "Inicio")
    object Stats : NavItem("statistics", iconRes = R.drawable.statistics, label = "Estadísticas")
}

@Composable
fun Navbar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem.Camera,
        NavItem.Home,
        NavItem.Stats
    )
    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    item.iconVector?.let {
                        Icon(it, contentDescription = item.label)
                    } ?: item.iconRes?.let {
                        Icon(painterResource(id = it), contentDescription = item.label)
                    }
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ColorPrimary, // Primary color
                    selectedTextColor = ColorPrimary,
                    unselectedIconColor = Color.LightGray, // Secondary color
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color.Transparent,
                )
            )
        }
    }
}