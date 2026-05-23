package com.dailyflow.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.R
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.PrimaryPinkButton

@Composable
fun OnboardingScreen(
    onNavigateToFeatures: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = context.getSharedPreferences("dailyflow_prefs", android.content.Context.MODE_PRIVATE)
    val factory = OnboardingViewModelFactory(sharedPreferences)
    val viewModel: OnboardingViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Cat mascot illustration
        Image(
            painter = painterResource(id = R.drawable.ic_cat_mascot),
            contentDescription = "Gatito mascota",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Welcome text
        Text(
            text = "¡Hola! Soy tu asistente 🐱",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bienvenido a DailyFlow. Para empezar, ¿cómo te llamas?",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Name input
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            placeholder = { Text("Tu nombre") },
            isError = uiState.nameError != null,
            supportingText = if (uiState.nameError != null) {
                { Text(uiState.nameError ?: "") }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPink,
                unfocusedBorderColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Continue button
        Button(
            onClick = { viewModel.onSaveName(onNavigateToFeatures) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CircleShape,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryPinkButton)
        ) {
            Text(
                text = "Continuar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Solo tomará un minuto",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
