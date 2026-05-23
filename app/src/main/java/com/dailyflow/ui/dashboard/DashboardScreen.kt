package com.dailyflow.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.dailyflow.ui.components.DashboardBlockCard
import com.dailyflow.ui.components.DashboardTaskCard
import com.dailyflow.ui.components.DailyFlowFab
import com.dailyflow.ui.components.DailyFlowLogo
import com.dailyflow.ui.components.PriorityBadge
import com.dailyflow.ui.components.SectionHeader
import com.dailyflow.ui.components.TaskCard
import com.dailyflow.ui.theme.BackgroundApp
import com.dailyflow.ui.theme.PrimaryPink
import com.dailyflow.ui.theme.ProgressFill
import com.dailyflow.ui.theme.ProgressTrack
import com.dailyflow.ui.theme.SurfaceCard
import com.dailyflow.ui.theme.TextPink
import com.dailyflow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTaskList: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToChooseType: () -> Unit,
    onNavigateToTaskForm: (Int) -> Unit = {}, // ✅ AGREGADO: Navegar a editar tarea
    onNavigateToBlockForm: (Int) -> Unit = {} // ✅ AGREGADO: Navegar a editar bloque
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as DailyFlowApplication
    val factory = DashboardViewModelFactory(
        application.taskRepository,
        application.blockRepository
    )
    val viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    // ✅ CORREGIDO: Obtener nombre de usuario desde SharedPreferences
    val userName = context.getSharedPreferences("dailyflow_prefs", android.content.Context.MODE_PRIVATE)
        .getString("user_name", "Usuario") ?: "Usuario"

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp),
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = {
                    DailyFlowLogo(fontSize = 24.sp)
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            DailyFlowFab(onClick = onNavigateToChooseType)
        },
        bottomBar = {
            // TODO: Implementar BottomNavigationBar cuando tengamos navegación completa
            Box(modifier = Modifier.height(1.dp))
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saludo
            item {
                Column {
                    Text(
                        text = "¡Hola $userName!", // ✅ CORREGIDO: Usar variable local
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.currentDate,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Card de progreso
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso de hoy",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.progressPercentage}%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryPink
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uiState.progressPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(6.dp)),
                            color = ProgressFill,
                            trackColor = ProgressTrack,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${uiState.completedTasksCount} de ${uiState.totalTasksCount} tareas completadas",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Card de tareas pendientes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        SectionHeader("Tareas pendientes", uiState.pendingTasks.size)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (uiState.pendingTasks.isEmpty()) {
                            Text(
                                text = "No hay tareas pendientes para hoy",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            uiState.pendingTasks.forEach { task ->
                                DashboardTaskCard(
                                    task = task,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    onClick = { onNavigateToTaskForm(task.id) }, // ✅ Click en la tarjeta para editar
                                    onToggleComplete = { // ✅ Click en el botón de completar
                                        viewModel.onTaskStatusChange(task, task.status != com.dailyflow.domain.model.TaskStatus.DONE)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        TextButton(
                            onClick = onNavigateToTaskList,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Ver todas",
                                color = TextPink
                            )
                        }
                    }
                }
            }

            // Card de próximos bloques
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        SectionHeader("Próximos bloques", uiState.upcomingBlocks.size)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (uiState.upcomingBlocks.isEmpty()) {
                            Text(
                                text = "No hay bloques programados",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        } else {
                            uiState.upcomingBlocks.forEach { block ->
                                DashboardBlockCard(
                                    block = block,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    onClick = { onNavigateToBlockForm(block.id) },
                                    onDelete = { viewModel.deleteBlock(block) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        TextButton(
                            onClick = onNavigateToSchedule,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Ver horario",
                                color = TextPink
                            )
                        }
                    }
                }
            }
        }
    }
}
