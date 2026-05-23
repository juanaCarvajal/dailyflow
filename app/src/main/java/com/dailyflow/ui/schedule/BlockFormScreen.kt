package com.dailyflow.ui.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.DailyFlowApplication
import com.dailyflow.domain.model.DayOfWeek
import com.dailyflow.ui.components.ColorPickerRow
import com.dailyflow.ui.components.FormLabel
import com.dailyflow.ui.components.OptionToggleRow
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.PrimaryPinkButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockFormScreen(
    blockId: Int,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = BlockFormViewModelFactory(
        application.blockRepository,
        blockId
    )
    val viewModel: BlockFormViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Editar bloque" else "Nuevo bloque"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Nombre
                FormLabel("Nombre del bloque")
                OutlinedTextField(
                    value = uiState.label,
                    onValueChange = { viewModel.onLabelChange(it) },
                    placeholder = { Text("Ej. Estudiar matemáticas") },
                    isError = uiState.labelError != null,
                    supportingText = if (uiState.labelError != null) {
                        { Text(uiState.labelError ?: "") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPink,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha
                FormLabel("Fecha")
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = uiState.selectedDate
                        }
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                                val selectedCalendar = Calendar.getInstance()
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                                viewModel.onDateChange(selectedCalendar.timeInMillis)
                            },
                            year,
                            month,
                            day
                        )
                        datePickerDialog.show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("dd MMMM, yyyy", Locale("es")).format(Date(uiState.selectedDate))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hora inicio y fin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    // Hora inicio
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Hora inicio")
                        TextButton(
                            onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = uiState.selectedStartTime
                                }
                                TimePickerDialog(
                                    context,
                                    { _: TimePicker, hourOfDay: Int, minute: Int ->
                                        // ✅ CORREGIDO: Usar la fecha seleccionada + hora seleccionada
                                        val dateCalendar = Calendar.getInstance().apply {
                                            timeInMillis = uiState.selectedDate
                                        }
                                        dateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        dateCalendar.set(Calendar.MINUTE, minute)
                                        dateCalendar.set(Calendar.SECOND, 0)
                                        dateCalendar.set(Calendar.MILLISECOND, 0)
                                        viewModel.onStartTimeChange(dateCalendar.timeInMillis)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(SimpleDateFormat("HH:mm", Locale("es")).format(Date(uiState.selectedStartTime)))
                        }
                    }

                    // Hora fin
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Hora fin")
                        TextButton(
                            onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = uiState.selectedEndTime
                                }
                                TimePickerDialog(
                                    context,
                                    { _: TimePicker, hourOfDay: Int, minute: Int ->
                                        // ✅ CORREGIDO: Usar la fecha seleccionada + hora seleccionada
                                        val dateCalendar = Calendar.getInstance().apply {
                                            timeInMillis = uiState.selectedDate
                                        }
                                        dateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        dateCalendar.set(Calendar.MINUTE, minute)
                                        dateCalendar.set(Calendar.SECOND, 0)
                                        dateCalendar.set(Calendar.MILLISECOND, 0)
                                        viewModel.onEndTimeChange(dateCalendar.timeInMillis)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(SimpleDateFormat("HH:mm", Locale("es")).format(Date(uiState.selectedEndTime)))
                        }
                    }
                }

                // Duración
                val durationMinutes = ((uiState.selectedEndTime - uiState.selectedStartTime) / 60000).toInt()
                Text(
                    text = "Duración: $durationMinutes minutos",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (uiState.timeError != null) {
                    Text(
                        text = uiState.timeError ?: "",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Día de la semana (selector)
                FormLabel("Día de la semana")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.dailyflow.domain.model.DayOfWeek.entries.forEach { dayOfWeek ->
                        DayOfWeekSelector(
                            dayOfWeek = dayOfWeek,
                            isSelected = uiState.selectedDayOfWeek == dayOfWeek,
                            onClick = { viewModel.onDayOfWeekChange(dayOfWeek) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Color
                FormLabel("Color del bloque")
                ColorPickerRow(
                    colors = listOf(
                        Color(0xFFF48FB1),
                        Color(0xFFCE93D8),
                        Color(0xFF90CAF9),
                        Color(0xFF80CBC4),
                        Color(0xFFFFF176),
                        Color(0xFFFFCC80)
                    ),
                    selected = Color(uiState.selectedColor),
                    onSelect = { color -> viewModel.onColorChange(color.hashCode()) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Opciones adicionales
                OptionToggleRow(
                    icon = Icons.Outlined.Repeat,
                    title = "Bloque repetitivo",
                    subtitle = "Repetir todos los días de la semana",
                    checked = uiState.isRepetitive,
                    onCheckedChange = { viewModel.onRepetitiveChange(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OptionToggleRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Recordarme antes",
                    subtitle = "Notificación 15 minutos antes",
                    checked = uiState.hasReminder,
                    onCheckedChange = { viewModel.onReminderChange(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botón guardar
            Button(
                onClick = { viewModel.onSaveBlock(onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                shape = CircleShape,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryPinkButton)
            ) {
                Text(
                    text = "Guardar bloque",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

// ✅ NUEVO COMPONENTE: Selector de día de la semana
@Composable
private fun DayOfWeekSelector(
    dayOfWeek: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        PrimaryPink.copy(alpha = 0.3f)
    } else {
        Color(0xFFF0F0F0)
    }

    val textColor = if (isSelected) {
        PrimaryPink
    } else {
        Color.Gray
    }

    Box(
        modifier = Modifier
            .height(40.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayOfWeek.displayName,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}
