package com.dailyflow.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyflow.data.repository.BlockRepository

class BlockFormViewModelFactory(
    private val blockRepository: BlockRepository,
    private val blockId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockFormViewModel::class.java)) {
            return BlockFormViewModel(blockRepository, blockId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
