package com.dailyflow.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyflow.data.repository.CategoryRepository
import com.dailyflow.data.repository.TaskRepository

class CategoriesViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(categoryRepository, taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
