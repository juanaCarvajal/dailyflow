package com.dailyflow.ui.onboarding

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, nameError = null)
    }

    fun onSaveName(onNavigateToFeatures: () -> Unit) {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            // Save name to SharedPreferences
            sharedPreferences.edit()
                .putString("user_name", _uiState.value.name)
                .putBoolean("onboarding_completed", true)
                .apply()

            onNavigateToFeatures()
        }
    }

    private fun validateForm(): Boolean {
        if (_uiState.value.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "Por favor ingresa tu nombre")
            return false
        }
        return true
    }
}

data class OnboardingUiState(
    val name: String = "",
    val nameError: String? = null
)

object OnboardingPreferences {
    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences("dailyflow_prefs", Context.MODE_PRIVATE)
        return prefs.getString("user_name", "") ?: ""
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        val prefs = context.getSharedPreferences("dailyflow_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("onboarding_completed", false)
    }
}
