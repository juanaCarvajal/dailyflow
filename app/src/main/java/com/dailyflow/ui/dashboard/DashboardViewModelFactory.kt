package com.dailyflow.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyflow.data.repository.BlockRepository
import com.dailyflow.data.repository.TaskRepository

class DashboardViewModelFactory(
    private val taskRepository: TaskRepository,
    private val blockRepository: BlockRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(taskRepository, blockRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
