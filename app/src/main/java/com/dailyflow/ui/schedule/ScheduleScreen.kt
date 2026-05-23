package com.dailyflow.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.DailyFlowApplication
import com.dailyflow.domain.model.DayOfWeek
import com.dailyflow.ui.components.DayChip
import com.dailyflow.ui.schedule.components.BlockTimelineCard
import com.dailyflow.ui.schedule.components.TimeLabelColumn
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.SurfaceCard
import com.dailyflow.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateToBlockForm: (Int) -> Unit,
    onNavigateBack: () -> Unit = {} // ✅ AGREGADO: Callback para navegación atrás
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = ScheduleViewModelFactory(application.blockRepository)
    val viewModel: ScheduleViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Horario Semanal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getWeekRange(uiState.currentWeekStart),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = { // ✅ AGREGADO: Botón atrás
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onPreviousWeek() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Semana anterior"
                        )
                    }
                    IconButton(onClick = { viewModel.onNextWeek() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Siguiente semana"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToBlockForm(0) },
                containerColor = PrimaryPink
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "Crear bloque",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Day Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp), // ✅ REDUCIDO: De 16.dp a 8.dp padding horizontal
                horizontalArrangement = Arrangement.SpaceEvenly // ✅ CAMBIADO: SpaceEvenly en lugar de SpaceBetween
            ) {
                // ✅ CORREGIDO: Calcular fechas del mes para cada día
                val weekCalendar = Calendar.getInstance().apply {
                    timeInMillis = uiState.currentWeekStart
                }

                DayOfWeek.entries.forEach { dayOfWeek ->
                    val dayOfMonth = getDayOfMonthForWeekday(weekCalendar, dayOfWeek.value)

                    DayChip(
                        dayName = dayOfWeek.displayName,
                        dayNumber = dayOfMonth, // ✅ CORREGIDO: Fecha del mes (28, 29, etc) no día de semana (1, 2, etc)
                        isSelected = uiState.selectedDayOfWeek == dayOfWeek.value,
                        onClick = { viewModel.onDaySelected(dayOfWeek.value) }
                    )
                }
            }

            // Timeline
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Blocks timeline (ya incluye las horas dentro)
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ✅ CAMBIADO: Item para cada hora (00:00 - 23:00) en lugar de (08:00 - 21:00)
                    items(24) { hourIndex ->
                        val hour = hourIndex

                        // Fila de hora con potential blocks
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            // Time label (dentro del scroll)
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                val hourLabel = String.format("%02d:00", hour)
                                Text(
                                    text = hourLabel,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Área donde pueden aparecer bloques en esta hora
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                // Línea divisoria
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .align(Alignment.TopCenter)
                                        .background(Color.LightGray.copy(alpha = 0.3f))
                                )

                                // Bloques que empiezan en esta hora
                                val blocksStartingThisHour = uiState.blocksForSelectedDay
                                    .filter { block ->
                                        val blockCalendar = Calendar.getInstance().apply {
                                            timeInMillis = block.startTime
                                        }
                                        blockCalendar.get(Calendar.HOUR_OF_DAY) == hour
                                    }

                                blocksStartingThisHour.forEach { block ->
                                    val blockCalendar = Calendar.getInstance().apply {
                                        timeInMillis = block.startTime
                                    }
                                    val blockMinute = blockCalendar.get(Calendar.MINUTE)
                                    val blockHeightMinutes = block.durationMinutes

                                    Box(
                                        modifier = Modifier
                                            .offset(
                                                x = 0.dp,
                                                y = blockMinute.dp
                                            )
                                            .fillMaxWidth()
                                            .height(blockHeightMinutes.dp)
                                            .zIndex(1f)
                                    ) {
                                        BlockTimelineCard(
                                            block = block,
                                            modifier = Modifier.fillMaxSize(),
                                            onClick = { onNavigateToBlockForm(block.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Empty state cuando no hay bloques
                    if (uiState.blocksForSelectedDay.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(top = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No hay bloques para este día",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Toca + para crear uno",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getWeekRange(weekStartMillis: Long): String {
    val weekCalendar = Calendar.getInstance().apply {
        timeInMillis = weekStartMillis
    }

    val monday = weekCalendar.time

    weekCalendar.add(Calendar.DAY_OF_MONTH, 6)
    val sunday = weekCalendar.time

    val dateFormat = SimpleDateFormat("dd MMM", Locale("es"))

    return "${dateFormat.format(monday)} - ${dateFormat.format(sunday)}, ${weekCalendar.get(Calendar.YEAR)}"
}

// ✅ NUEVA FUNCIÓN: Calcular el día del mes para cada día de la semana
private fun getDayOfMonthForWeekday(weekStartCalendar: Calendar, dayOfWeek: Int): Int {
    val calendar = weekStartCalendar.clone() as Calendar
    // dayOfWeek: 1=Lunes, 2=Martes, ..., 7=Domingo
    calendar.add(Calendar.DAY_OF_MONTH, dayOfWeek - 1)
    return calendar.get(Calendar.DAY_OF_MONTH)
}
