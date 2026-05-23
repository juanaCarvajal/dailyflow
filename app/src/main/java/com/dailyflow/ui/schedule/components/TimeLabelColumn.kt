package com.dailyflow.ui.schedule.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyflow.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TimeLabelColumn(
    modifier: Modifier = Modifier
) {
    val hours = (8..21).toList() // 08:00 to 21:00

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            items(hours.size) { index ->
                val hour = hours[index]
                val timeLabel = SimpleDateFormat("HH:mm", Locale("es")).format(
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, 0)
                    }.time
                )

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = timeLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
