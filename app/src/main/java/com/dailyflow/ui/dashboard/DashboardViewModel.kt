package com.dailyflow.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyflow.data.repository.BlockRepository
import com.dailyflow.data.repository.TaskRepository
import com.dailyflow.domain.model.Block
import com.dailyflow.domain.model.Task
import com.dailyflow.domain.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardViewModel(
    private val taskRepository: TaskRepository,
    private val blockRepository: BlockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val today = Calendar.getInstance()
            val startOfDay = today.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = today.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            // ✅ CORREGIDO: Ahora escucha todas las tareas, no solo las de hoy
            combine(
                taskRepository.getAllTasks(), // ✅ CAMBIADO: getAllTasks en lugar de getTasksForToday
                taskRepository.getCompletedTasksToday(startOfDay, endOfDay),
                blockRepository.getAllBlocks()
            ) { allTasks, completedTasksToday, blocks ->
                // Filtrar tareas pendientes (no completadas) y ordenar por prioridad
                val pendingTasks = allTasks
                    .filter { it.status != TaskStatus.DONE }
                    .sortedWith(compareBy<Task> { it.priority }
                        .thenByDescending { it.createdAt })

                // ✅ CORREGIDO: Calcular progreso basado en tareas completadas hoy vs tareas totales
                val completedToday = completedTasksToday.size

                // Para el total, contar tareas que tienen deadline hoy O tareas sin deadline
                val totalToday = allTasks.count { task ->
                    (task.deadline != null && task.deadline >= startOfDay && task.deadline <= endOfDay) ||
                    task.deadline == null // Incluir tareas sin deadline
                }

                val progress = if (totalToday > 0) {
                    (completedToday.toFloat() / totalToday * 100).toInt()
                } else 0

                DashboardUiState(
                    userName = "", // ✅ CORREGIDO: Se obtiene en la Screen, no en el ViewModel
                    currentDate = SimpleDateFormat("dd MMM, yyyy", Locale("es")).format(Date()),
                    progressPercentage = progress,
                    completedTasksCount = completedToday,
                    totalTasksCount = totalToday,
                    pendingTasks = pendingTasks.take(3), // ✅ CORREGIDO: Muestra tareas pendientes (no solo de hoy)
                    upcomingBlocks = blocks.take(2)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // ✅ AGREGADO: Función para cambiar el estado de una tarea
    fun onTaskStatusChange(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                status = if (isCompleted) TaskStatus.DONE else TaskStatus.PENDING
            )
            taskRepository.updateTask(updatedTask)
        }
    }

    // ✅ AGREGADO: Función para eliminar un bloque
    fun deleteBlock(block: Block) {
        viewModelScope.launch {
            blockRepository.deleteBlock(block)
        }
    }
}

data class DashboardUiState(
    val userName: String = "",
    val currentDate: String = "",
    val progressPercentage: Int = 0,
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0,
    val pendingTasks: List<Task> = emptyList(),
    val upcomingBlocks: List<Block> = emptyList() // ✅ CORREGIDO: Tipo correcto Block
)
