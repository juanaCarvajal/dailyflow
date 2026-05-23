package com.dailyflow.ui.tasks

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background as background
import androidx.compose.foundation.clickable as clickable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.DailyFlowApplication
import com.dailyflow.domain.model.Priority
import com.dailyflow.ui.components.ColorPickerRow
import com.dailyflow.ui.components.FormLabel
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.PrimaryPinkButton
import com.dailyflow.ui.theme.SurfaceCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit = {} // ✅ AGREGADO: Navegar al dashboard después de guardar
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = TaskFormViewModelFactory(
        application.taskRepository,
        application.categoryRepository,
        taskId
    )
    val viewModel: TaskFormViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Editar tarea" else "Nueva tarea"
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
                FormLabel("Nombre")
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    placeholder = { Text("Ej. Estudiar cálculo") },
                    isError = uiState.nameError != null,
                    supportingText = if (uiState.nameError != null) {
                        { Text(uiState.nameError ?: "") }
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

                // Descripción
                FormLabel("Descripción (opcional)")
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    placeholder = { Text("Agregar detalles...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPink,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prioridad
                FormLabel("Prioridad")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        PriorityChipSimple(
                            priority = priority,
                            isSelected = uiState.selectedPriority == priority,
                            onClick = { viewModel.onPriorityChange(priority) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha límite
                FormLabel("Fecha límite")
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                                val selectedCalendar = Calendar.getInstance()
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                                viewModel.onDeadlineChange(selectedCalendar.timeInMillis)
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
                        text = if (uiState.deadline != null) {
                            SimpleDateFormat("dd MMM, yyyy", Locale("es")).format(Date(uiState.deadline!!))
                        } else {
                            "Seleccionar fecha"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Color del bloque
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
                    onSelect = { viewModel.onColorChange(it.toArgb()) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botón guardar (fijo en la parte inferior)
            Button(
                onClick = { viewModel.onSaveTask(onNavigateToDashboard) }, // ✅ CAMBIADO: Ir al dashboard después de guardar
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                shape = CircleShape,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryPinkButton)
            ) {
                Text(
                    text = "Guardar tarea",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun PriorityChipSimple(
    priority: Priority,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (backgroundColor, textColor) = when (priority) {
        Priority.HIGH -> Pair(
            if (isSelected) Color(0xFFFFCDD2) else Color(0xFFF0F0F0),
            if (isSelected) Color(0xFFB71C1C) else Color.Gray
        )
        Priority.MEDIUM -> Pair(
            if (isSelected) Color(0xFFFFF9C4) else Color(0xFFF0F0F0),
            if (isSelected) Color(0xFFF57F17) else Color.Gray
        )
        Priority.LOW -> Pair(
            if (isSelected) Color(0xFFC8E6C9) else Color(0xFFF0F0F0),
            if (isSelected) Color(0xFF1B5E20) else Color.Gray
        )
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp, 16.dp)
    ) {
        Text(
            text = when (priority) {
                Priority.HIGH -> "Alta"
                Priority.MEDIUM -> "Media"
                Priority.LOW -> "Baja"
            },
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}
