package edu.mx.utleon.cabmobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import edu.mx.utleon.cabmobile.ui.theme.ColorInfo
import edu.mx.utleon.cabmobile.ui.theme.ColorPrimary
import edu.mx.utleon.cabmobile.R
import edu.mx.utleon.cabmobile.ui.theme.ColorAlertOrError

data class ZoneDetection(
    val zoneName: String,
    val deviceId: String,
    val recyclableCount: Int,
    val organicCount: Int,
    val nonRecyclableCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistics(navigation: NavHostController) {
    val zoneDetections = listOf(
        ZoneDetection("CVD Baños", "ESP32-CAM", 1, 1, 1),
        ZoneDetection("Edificio D Pasillo", "ESP32-CAM", 4, 2, 5),
        ZoneDetection("Edificio A Entrada", "ESP32-CAM", 7, 0, 12)
    )

    val totalDetections = zoneDetections.sumOf {
        it.recyclableCount + it.organicCount + it.nonRecyclableCount
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorPrimary)
        ) {
            TopAppBar(
                title = {
                Text(
                    "Estadísticas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(end = 32.dp),
                    textAlign = TextAlign.Center
                )
            }, navigationIcon = {
                IconButton(onClick = { navigation.navigateUp() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                BarChart()
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(top = 60.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(zoneDetections) { zone ->
                        ZoneDetectionCard(zone = zone)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
                .offset(y = (-60).dp)
                .align(Alignment.Center)
                .zIndex(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ColorInfo),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Basurero",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Total de detecciones",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "del día de hoy",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = totalDetections.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BarChart() {
    val barHeights = listOf(0.8f, 0.4f, 0.6f, 0.9f, 0.2f, 0.7f, 0.5f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        barHeights.forEach { height ->
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height((150 * height).dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
fun ZoneDetectionCard(zone: ZoneDetection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ESP32-CAM Image
        Image(
            painter = painterResource(R.drawable.esp32), // Replace with actual ESP32-CAM image
            contentDescription = "ESP32-CAM", modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = zone.zoneName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Detection counters
            Row(
                horizontalArrangement = Arrangement.spacedBy(64.dp)
            ) {
                DetectionCounter(
                    count = zone.recyclableCount, resource = R.drawable.valorizable
                )
                DetectionCounter(
                    count = zone.organicCount, resource = R.drawable.organico
                )
                DetectionCounter(
                    count = zone.nonRecyclableCount, resource = R.drawable.no_valorizable
                )
            }
        }
    }
}

@Composable
fun DetectionCounter(
    count: Int,
    resource: Int
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painterResource(resource),
            contentDescription = "Cámara",
            modifier = Modifier.size(
                32.dp
            ),
            alignment = Alignment.Center
        )

        Text(
            text = count.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAlertOrError
        )
    }
}
