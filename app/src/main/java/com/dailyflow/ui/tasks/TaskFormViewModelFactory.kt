package com.dailyflow.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyflow.data.repository.CategoryRepository
import com.dailyflow.data.repository.TaskRepository

class TaskFormViewModelFactory(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskFormViewModel(taskRepository, categoryRepository, taskId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
