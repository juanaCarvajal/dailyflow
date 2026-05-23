package com.dailyflow.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyflow.data.repository.CategoryRepository
import com.dailyflow.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryFormViewModel(
    private val categoryRepository: CategoryRepository,
    private val categoryId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryFormUiState())
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    init {
        if (categoryId > 0) {
            loadCategory()
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            val category = categoryRepository.getCategoryById(categoryId)
            category?.let {
                _uiState.value = _uiState.value.copy(
                    name = it.name,
                    selectedColor = android.graphics.Color.parseColor(it.colorHex),
                    selectedIcon = it.iconName,
                    isEditMode = true
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null)
    }

    fun onColorChange(color: Int) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun onIconChange(icon: String) {
        _uiState.value = _uiState.value.copy(selectedIcon = icon)
    }

    fun onSaveCategory(onNavigateBack: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val category = Category(
            id = categoryId,
            name = _uiState.value.name,
            colorHex = String.format("#%06X", (0xFFFFFF and _uiState.value.selectedColor)),
            iconName = _uiState.value.selectedIcon
        )

        viewModelScope.launch {
            if (categoryId > 0) {
                categoryRepository.updateCategory(category)
            } else {
                categoryRepository.insertCategory(category)
            }
            onNavigateBack()
        }
    }

    private fun validateForm(): Boolean {
        if (_uiState.value.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "El nombre es obligatorio")
            return false
        }
        return true
    }
}

data class CategoryFormUiState(
    val name: String = "",
    val nameError: String? = null,
    val selectedColor: Int = 0xFF6200EE.toInt(),
    val selectedIcon: String = "label",
    val isEditMode: Boolean = false
)
