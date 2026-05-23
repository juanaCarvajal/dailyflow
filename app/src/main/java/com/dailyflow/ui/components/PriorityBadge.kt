package com.dailyflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyflow.domain.model.Priority
import com.dailyflow.ui.theme.PriorityHigh
import com.dailyflow.ui.theme.PriorityLow
import com.dailyflow.ui.theme.PriorityMedium

@Composable
fun PriorityBadge(priority: Priority) {
    val (backgroundColor, textColor, label) = when (priority) {
        Priority.HIGH -> Triple(
            Color(0xFFFFCDD2),
            Color(0xFFB71C1C),
            "Alta"
        )
        Priority.MEDIUM -> Triple(
            Color(0xFFFFF9C4),
            Color(0xFFF57F17),
            "Media"
        )
        Priority.LOW -> Triple(
            Color(0xFFC8E6C9),
            Color(0xFF1B5E20),
            "Baja"
        )
    }

    Row(
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .padding(6.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .background(
                    when (priority) {
                        Priority.HIGH -> PriorityHigh
                        Priority.MEDIUM -> PriorityMedium
                        Priority.LOW -> PriorityLow
                    },
                    CircleShape
                )
                .size(8.dp)
        ) {}
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
