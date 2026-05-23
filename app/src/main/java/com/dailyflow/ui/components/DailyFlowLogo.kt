package com.dailyflow.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dailyflow.ui.theme.TextPink
import com.dailyflow.ui.theme.TextPrimary
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun DailyFlowLogo(fontSize: TextUnit = 28.sp) {
    androidx.compose.material3.Text(
        text = buildAnnotatedString {
            withStyle(androidx.compose.ui.text.SpanStyle(
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize
            )) {
                append("Daily")
            }
            withStyle(androidx.compose.ui.text.SpanStyle(
                color = TextPink,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize
            )) {
                append("Flow")
            }
        }
    )
}
