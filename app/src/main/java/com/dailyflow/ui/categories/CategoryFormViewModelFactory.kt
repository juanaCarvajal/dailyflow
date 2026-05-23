package com.dailyflow.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyflow.data.repository.CategoryRepository

class CategoryFormViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val categoryId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryFormViewModel::class.java)) {
            return CategoryFormViewModel(categoryRepository, categoryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
