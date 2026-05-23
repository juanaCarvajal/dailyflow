package com.dailyflow.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.MintGreen
import com.dailyflow.ui.theme.MintGreenLight
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.PrimaryPinkButton

@Composable
fun FeaturesScreen(
    userName: String,
    onNavigateToDashboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Welcome with name
        Text(
            text = "¡Hola, $userName! 🎉",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Todo listo para organizar tu día",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Feature cards
        FeatureCard(
            icon = "✅",
            title = "Gestiona tus tareas",
            description = "Crea, organiza y completa tus actividades diarias",
            color = PrimaryPink
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(
            icon = "⏰",
            title = "Planifica tu tiempo",
            description = "Reserva bloques de tiempo para enfocarte en lo importante",
            color = MintGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(
            icon = "📊",
            title = "Mide tu progreso",
            description = "Visualiza tu productividad y mejora cada día",
            color = PrimaryPink
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Start button
        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryPinkButton)
        ) {
            Text(
                text = "Comenzar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Comenzar",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FeatureCard(
    icon: String,
    title: String,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = color.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Check icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
