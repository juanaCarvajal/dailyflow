package com.dailyflow.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyflow.data.repository.BlockRepository

class ScheduleViewModelFactory(
    private val blockRepository: BlockRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            return ScheduleViewModel(blockRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
