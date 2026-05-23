package com.dailyflow.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val DailyFlowShapes = Shapes(
    small = RoundedCornerShape(12.dp),   // Chips, badges, campos
    medium = RoundedCornerShape(16.dp),   // Cards de categoría y tarea
    large = RoundedCornerShape(24.dp),    // Cards del dashboard
    extraLarge = RoundedCornerShape(50.dp) // Botones CTA, FAB, chips activos
)
