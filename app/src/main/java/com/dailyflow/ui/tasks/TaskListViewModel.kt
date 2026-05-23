package com.dailyflow.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyflow.data.repository.TaskRepository
import com.dailyflow.domain.model.Priority
import com.dailyflow.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadAllTasks()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(
                    allTasks = tasks,
                    filteredTasks = applyFilter(tasks, _uiState.value.selectedFilter)
                )
            }
        }
    }

    fun onFilterChange(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredTasks = applyFilter(_uiState.value.allTasks, filter)
        )
    }

    fun onTaskStatusChange(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                status = if (isCompleted) com.dailyflow.domain.model.TaskStatus.DONE
                else com.dailyflow.domain.model.TaskStatus.PENDING
            )
            taskRepository.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    // ✅ AGREGADO: Función para borrar todas las tareas completadas
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                val completedTasks = tasks.filter { it.status == com.dailyflow.domain.model.TaskStatus.DONE }
                completedTasks.forEach { task ->
                    taskRepository.deleteTask(task)
                }
            }
        }
    }

    private fun applyFilter(tasks: List<Task>, filter: TaskFilter): List<Task> {
        return when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.HIGH -> tasks.filter { it.priority == Priority.HIGH }
            TaskFilter.MEDIUM -> tasks.filter { it.priority == Priority.MEDIUM }
            TaskFilter.LOW -> tasks.filter { it.priority == Priority.LOW }
        }
    }
}

data class TaskListUiState(
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL
)

enum class TaskFilter {
    ALL, HIGH, MEDIUM, LOW
}
