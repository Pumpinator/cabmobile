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
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class EstadisticasResponse(
    val resumen: ResumenEstadisticas,
    val porTipo: List<EstadisticaTipo>,
    val porZona: List<EstadisticaZona>,
    val porHora: List<EstadisticaHora>,
    val tendenciaMensual: List<TendenciaMensual>
)

data class ResumenEstadisticas(
    val totalDetecciones: Int,
    val deteccionesHoy: Int,
    val deteccionesEsteMes: Int,
    val deteccionesEsteAno: Int
)

data class EstadisticaTipo(
    val tipo: String,
    val cantidad: Int
)

data class EstadisticaZona(
    val zona: String,
    val cantidad: Int
)

data class EstadisticaHora(
    val hora: Int,
    val cantidad: Int
)

data class TendenciaMensual(
    val mes: Int,
    val ano: Int,
    val total: Int,
    val organicos: Int,
    val valorizables: Int,
    val noValorizables: Int
)

data class EstadisticasZonasResponse(
    val zonaId: Int,
    val zonaNombre: String,
    val totalDetecciones: Int,
    val organicos: Int,
    val valorizables: Int,
    val noValorizables: Int,
    val porcentaje: Double
)

// Retrofit API interface
interface DeteccionesApi {
    @GET("api/detecciones/estadisticas")
    suspend fun getEstadisticas(
        @Header("Authorization") token: String
    ): EstadisticasResponse

    @GET("api/detecciones/estadisticas/zonas")
    suspend fun getEstadisticasZonas(
        @Header("Authorization") token: String
    ): List<EstadisticasZonasResponse>

    @GET("api/detecciones/estadisticas/horarios-recurrentes")
    suspend fun getHorariosRecurrentes(
        @Header("Authorization") token: String
    ): List<HorarioRecurrente>
}

data class HorarioRecurrente(
    val hora: Int,
    val cantidad: Int,
    val porcentaje: Double,
    val tipoMasComun: String
)

// Retrofit instance
object ApiClient {
    private const val BASE_URL = "https://localhost:7286/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val deteccionesApi: DeteccionesApi = retrofit.create(DeteccionesApi::class.java)
}

// Estados para la UI
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistics(navigation: NavHostController) {
    var estadisticasState by remember { mutableStateOf<UiState<EstadisticasResponse>>(UiState.Loading) }
    var zonasState by remember { mutableStateOf<UiState<List<EstadisticasZonasResponse>>>(UiState.Loading) }
    var horariosState by remember { mutableStateOf<UiState<List<HorarioRecurrente>>>(UiState.Loading) }

    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val authManager = remember { AuthManager(context) }

    // Función para cargar datos
    fun loadData() {
        scope.launch {
            try {
                val token = authManager.getBearerToken()
                if (token == null) {
                    estadisticasState = UiState.Error("No hay token de autenticación")
                    return@launch
                }

                // Cargar estadísticas generales
                estadisticasState = UiState.Loading
                val estadisticas = ApiClient.deteccionesApi.getEstadisticas(token)
                estadisticasState = UiState.Success(estadisticas)

                // Cargar estadísticas por zonas
                zonasState = UiState.Loading
                val zonas = ApiClient.deteccionesApi.getEstadisticasZonas(token)
                zonasState = UiState.Success(zonas)

                // Cargar horarios recurrentes
                horariosState = UiState.Loading
                val horarios = ApiClient.deteccionesApi.getHorariosRecurrentes(token)
                horariosState = UiState.Success(horarios)

            } catch (e: Exception) {
                estadisticasState = UiState.Error(e.message ?: "Error desconocido")
                zonasState = UiState.Error(e.message ?: "Error desconocido")
                horariosState = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        loadData()
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
                },
                navigationIcon = {
                    IconButton(onClick = { navigation.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Gráfica de barras con datos reales
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = horariosState) {
                    is UiState.Success -> {
                        BarChart(horarios = state.data.take(7)) // Mostrar top 7 horas
                    }
                    is UiState.Loading -> {
                        CircularProgressIndicator(color = Color.White)
                    }
                    is UiState.Error -> {
                        Text(
                            text = "Error al cargar gráfica",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(top = 60.dp)
            ) {
                when (val state = zonasState) {
                    is UiState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.data) { zona ->
                                ZoneDetectionCard(zona = zona)
                            }
                        }
                    }
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error al cargar zonas",
                                    fontSize = 16.sp,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { loadData() }) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Card flotante con total de detecciones
        when (val state = estadisticasState) {
            is UiState.Success -> {
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
                            text = state.data.resumen.deteccionesHoy.toString(),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            is UiState.Loading -> {
                // Mostrar un placeholder mientras carga
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
            is UiState.Error -> {
                // Mostrar error
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                        .offset(y = (-60).dp)
                        .align(Alignment.Center)
                        .zIndex(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorAlertOrError),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error al cargar",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarChart(horarios: List<HorarioRecurrente>) {
    if (horarios.isEmpty()) {
        Text(
            text = "No hay datos disponibles",
            color = Color.White,
            fontSize = 14.sp
        )
        return
    }

    val maxCantidad = horarios.maxOfOrNull { it.cantidad } ?: 1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        horarios.forEach { horario ->
            val height = (horario.cantidad.toFloat() / maxCantidad.toFloat()).coerceIn(0.1f, 1.0f)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height((150 * height).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${horario.hora}h",
                    color = Color.White,
                    fontSize = 10.sp
                )

                Text(
                    text = horario.cantidad.toString(),
                    color = Color.White,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun ZoneDetectionCard(zona: EstadisticasZonasResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ESP32-CAM Image
        Image(
            painter = painterResource(R.drawable.esp32),
            contentDescription = "ESP32-CAM",
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = zona.zonaNombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E)
            )

            Text(
                text = "Total: ${zona.totalDetecciones} detecciones",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = "${zona.porcentaje}% del total",
                fontSize = 12.sp,
                color = ColorInfo
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Detection counters
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                DetectionCounter(
                    count = zona.valorizables,
                    resource = R.drawable.valorizable,
                    label = "Valorizable"
                )
                DetectionCounter(
                    count = zona.organicos,
                    resource = R.drawable.organico,
                    label = "Orgánico"
                )
                DetectionCounter(
                    count = zona.noValorizables,
                    resource = R.drawable.no_valorizable,
                    label = "No valorizable"
                )
            }
        }
    }
}

@Composable
fun DetectionCounter(
    count: Int,
    resource: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Image(
            painterResource(resource),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            alignment = Alignment.Center
        )

        Text(
            text = count.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAlertOrError
        )

        Text(
            text = label,
            fontSize = 8.sp,
            color = Color.Gray,
            maxLines = 1
        )
    }
}