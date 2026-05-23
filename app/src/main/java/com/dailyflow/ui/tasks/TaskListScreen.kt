package com.dailyflow.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyflow.DailyFlowApplication
import com.dailyflow.domain.model.TaskStatus
import com.dailyflow.ui.components.TaskCard
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToTaskForm: (Int) -> Unit,
    onNavigateToChooseType: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = TaskListViewModelFactory(application.taskRepository)
    val viewModel: TaskListViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = { Text("Tareas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Borrar historial",
                            tint = Color(0xFFF44336)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToChooseType,
                containerColor = PrimaryPink
            ) {
                Icon(Icons.Default.Add, "Crear", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mis Tareas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Filtros por prioridad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilterChip(
                    label = "Todas",
                    isSelected = uiState.selectedFilter == TaskFilter.ALL,
                    onClick = { viewModel.onFilterChange(TaskFilter.ALL) }
                )
                TaskFilterChip(
                    label = "Alta",
                    isSelected = uiState.selectedFilter == TaskFilter.HIGH,
                    color = Color(0xFFFF5252),
                    onClick = { viewModel.onFilterChange(TaskFilter.HIGH) }
                )
                TaskFilterChip(
                    label = "Media",
                    isSelected = uiState.selectedFilter == TaskFilter.MEDIUM,
                    color = Color(0xFFFFB300),
                    onClick = { viewModel.onFilterChange(TaskFilter.MEDIUM) }
                )
                TaskFilterChip(
                    label = "Baja",
                    isSelected = uiState.selectedFilter == TaskFilter.LOW,
                    color = Color(0xFF4CAF50),
                    onClick = { viewModel.onFilterChange(TaskFilter.LOW) }
                )
            }

            // Lista de tareas
            if (uiState.filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay tareas disponibles",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 20.dp,
                        vertical = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onCheck = { viewModel.onTaskStatusChange(task, task.status != TaskStatus.DONE) },
                            onClick = { onNavigateToTaskForm(task.id) }
                        )
                    }
                }
            }
        }

        // Diálogo de confirmación para borrar historial
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        text = "Borrar historial de tareas",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que quieres eliminar todas las tareas completadas? Esta acción no se puede deshacer."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCompletedTasks()
                            showDeleteDialog = false
                        }
                    ) {
                        Text(
                            text = "Borrar",
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun TaskFilterChip(
    label: String,
    isSelected: Boolean,
    color: Color = Color(0xFF9E9E9E),
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        color.copy(alpha = 0.15f)
    } else {
        Color(0xFFF5F5F5)
    }

    val textColor = if (isSelected) {
        color
    } else {
        Color(0xFF757575)
    }

    val borderColor = if (isSelected) {
        color.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(8.dp)
                )
            }
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}
