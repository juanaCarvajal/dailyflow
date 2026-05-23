package com.dailyflow.ui.schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyflow.domain.model.Block
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BlockTimelineCard(
    block: Block,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(block.colorHex))
    } catch (e: Exception) {
        Color(0xFF80CBC4)
    }

    val timeRange = "${formatTime(block.startTime)} - ${formatTime(block.startTime + block.durationMinutes * 60000)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = block.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = timeRange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale("es")).format(Date(timestamp))
}
