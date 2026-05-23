package com.dailyflow.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyflow.data.repository.TaskRepository
import com.dailyflow.data.repository.CategoryRepository
import com.dailyflow.domain.model.Priority
import com.dailyflow.domain.model.Task
import com.dailyflow.domain.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskFormViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskFormUiState())
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    init {
        if (taskId > 0) {
            loadTask(taskId)
        }
        loadCategories()
    }

    private fun loadTask(id: Int) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(id)
            task?.let {
                _uiState.value = _uiState.value.copy(
                    name = it.name,
                    description = it.description,
                    selectedPriority = it.priority,
                    deadline = it.deadline,
                    selectedColor = android.graphics.Color.parseColor(it.colorHex),
                    selectedCategoryId = if (it.categoryIds.isNotEmpty()) it.categoryIds[0] else null,
                    isEditMode = true
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(
                    availableCategories = categories
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.value = _uiState.value.copy(selectedPriority = priority)
    }

    fun onDeadlineChange(deadline: Long?) {
        _uiState.value = _uiState.value.copy(deadline = deadline)
    }

    fun onColorChange(color: Int) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun onCategoryChange(categoryId: Int?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }

    fun onSaveTask(onSuccess: () -> Unit) {
        if (_uiState.value.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "El nombre es obligatorio")
            return
        }

        viewModelScope.launch {
            val task = Task(
                id = if (_uiState.value.isEditMode) taskId else 0,
                name = _uiState.value.name.trim(),
                description = _uiState.value.description.trim(),
                priority = _uiState.value.selectedPriority,
                deadline = _uiState.value.deadline,
                status = if (_uiState.value.isEditMode) TaskStatus.PENDING else TaskStatus.PENDING,
                categoryIds = if (_uiState.value.selectedCategoryId != null)
                    listOf(_uiState.value.selectedCategoryId!!) else emptyList(),
                colorHex = String.format("#%06X", (0xFFFFFF and _uiState.value.selectedColor))
            )

            if (_uiState.value.isEditMode) {
                taskRepository.updateTask(task)
            } else {
                taskRepository.insertTask(task)
            }
            onSuccess()
        }
    }
}

data class TaskFormUiState(
    val name: String = "",
    val description: String = "",
    val selectedPriority: Priority = Priority.MEDIUM,
    val deadline: Long? = null,
    val selectedColor: Int = android.graphics.Color.parseColor("#F06292"),
    val selectedCategoryId: Int? = null,
    val availableCategories: List<com.dailyflow.domain.model.Category> = emptyList(),
    val isEditMode: Boolean = false,
    val nameError: String? = null
)
