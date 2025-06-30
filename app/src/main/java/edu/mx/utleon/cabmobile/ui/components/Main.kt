package edu.mx.utleon.cabmobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import edu.mx.utleon.cabmobile.ui.theme.ColorPrimary
import kotlinx.coroutines.delay
import edu.mx.utleon.cabmobile.R
import edu.mx.utleon.cabmobile.ui.theme.ColorAlertOrError
import edu.mx.utleon.cabmobile.ui.theme.ColorBackground
import edu.mx.utleon.cabmobile.ui.theme.ColorSecondary

data class TipData(
    val title: String,
    val description: String,
    val mascotImage: Int // Resource ID for the mascot image
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(navigation: NavHostController) {
    val tips = listOf(
        TipData(
            title = "Para reducir el consumo de plástico...",
            description = "Evita comprar productos en envases de plástico y opta por opciones de cartón o vidrio cuando sea posible. Además, comprar a granel también puede ayudar a reducir el uso de plásticos.",
            mascotImage = R.drawable.basurin1 // Placeholder - replace with actual mascot image
        ),
        TipData(
            title = "Para separar correctamente tus residuos...",
            description = "Identifica si el residuo es orgánico (restos de comida), valorizable (botellas, papel, latas) o no valorizable (papeles sucios, envolturas contaminadas) antes de tirarlo.",
            mascotImage = R.drawable.basurin2 // Placeholder - replace with actual mascot image
        ),
        TipData(
            title = "Para evitar contaminar materiales reciclables...",
            description = "No mezcles residuos orgánicos con reciclables.",
            mascotImage = R.drawable.basurin3 // Placeholder - replace with actual mascot image
        ),
        TipData(
            title = "Para reducir tu impacto ambiental diario...",
            description = "Lleva tu termo, cubiertos reutilizables y bolsa de tela. Así evitarás usar productos desechables innecesarios como botellas, bolsas y vasos plásticos.",
            mascotImage = R.drawable.basurin4 // Placeholder - replace with actual mascot image
        )
    )

    val pagerState = rememberPagerState(pageCount = { tips.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000) // Wait 4 seconds
            val nextPage = (pagerState.currentPage + 1) % tips.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Inicio",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_dialog_info),
                        contentDescription = "Tip",
                        tint = ColorPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TIP",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    TipCard(tip = tips[page])
                }

                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(tips.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == pagerState.currentPage)
                                        ColorPrimary
                                    else
                                        Color.LightGray
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Recycling Symbol",
                tint = ColorPrimary,
                modifier = Modifier.size(256.dp)
            )
        }
    }
}

@Composable
fun TipCard(tip: TipData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mascot Image
        Image(
            painter = painterResource(tip.mascotImage),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tip Title
        Text(
            text = tip.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tip Description
        Text(
            text = tip.description,
            fontSize = 14.sp,
            color = ColorAlertOrError,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
