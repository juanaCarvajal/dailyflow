package com.dailyflow.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyflow.data.repository.BlockRepository
import com.dailyflow.domain.model.Block
import com.dailyflow.domain.model.DayOfWeek
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class BlockFormViewModel(
    private val blockRepository: BlockRepository,
    private val blockId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockFormUiState())
    val uiState: StateFlow<BlockFormUiState> = _uiState.asStateFlow()

    init {
        if (blockId > 0) {
            loadBlock()
        } else {
            // Set default values for new block
            val calendar = Calendar.getInstance()
            _uiState.value = _uiState.value.copy(
                selectedDate = calendar.timeInMillis,
                selectedStartTime = calendar.timeInMillis,
                selectedEndTime = calendar.apply { add(Calendar.HOUR_OF_DAY, 1) }.timeInMillis
            )
        }
    }

    private fun loadBlock() {
        viewModelScope.launch {
            val block = blockRepository.getBlockById(blockId)
            block?.let {
                _uiState.value = _uiState.value.copy(
                    label = it.label,
                    selectedDate = it.startTime,
                    selectedStartTime = it.startTime,
                    selectedEndTime = it.startTime + it.durationMinutes * 60000,
                    selectedDayOfWeek = it.dayOfWeek,
                    selectedColor = android.graphics.Color.parseColor(it.colorHex),
                    isRepetitive = it.isRepetitive,
                    hasReminder = it.hasReminder,
                    isEditMode = true
                )
            }
        }
    }

    fun onLabelChange(label: String) {
        _uiState.value = _uiState.value.copy(label = label, labelError = null)
    }

    fun onDateChange(timestamp: Long) {
        _uiState.value = _uiState.value.copy(selectedDate = timestamp)
        updateDayOfWeek(timestamp)
    }

    fun onStartTimeChange(timestamp: Long) {
        _uiState.value = _uiState.value.copy(selectedStartTime = timestamp)
        // Update end time to maintain duration
        val currentDuration = _uiState.value.selectedEndTime - _uiState.value.selectedStartTime
        val newEndTime = timestamp + currentDuration
        _uiState.value = _uiState.value.copy(selectedEndTime = newEndTime)
    }

    fun onEndTimeChange(timestamp: Long) {
        _uiState.value = _uiState.value.copy(selectedEndTime = timestamp)
    }

    fun onDayOfWeekChange(dayOfWeek: DayOfWeek) {
        _uiState.value = _uiState.value.copy(selectedDayOfWeek = dayOfWeek)
    }

    fun onColorChange(color: Int) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun onRepetitiveChange(isRepetitive: Boolean) {
        _uiState.value = _uiState.value.copy(isRepetitive = isRepetitive)
    }

    fun onReminderChange(hasReminder: Boolean) {
        _uiState.value = _uiState.value.copy(hasReminder = hasReminder)
    }

    fun onSaveBlock(onNavigateBack: () -> Unit) {
        if (!validateForm()) {
            return
        }

        val durationMinutes = ((_uiState.value.selectedEndTime - _uiState.value.selectedStartTime) / 60000).toInt()

        val block = Block(
            id = blockId,
            label = _uiState.value.label,
            dayOfWeek = _uiState.value.selectedDayOfWeek,
            startTime = _uiState.value.selectedStartTime,
            durationMinutes = durationMinutes,
            colorHex = String.format("#%06X", (0xFFFFFF and _uiState.value.selectedColor)),
            isRepetitive = _uiState.value.isRepetitive,
            hasReminder = _uiState.value.hasReminder
        )

        viewModelScope.launch {
            if (blockId > 0) {
                blockRepository.updateBlock(block)
            } else {
                blockRepository.insertBlock(block)
            }
            onNavigateBack()
        }
    }

    private fun validateForm(): Boolean {
        if (_uiState.value.label.isBlank()) {
            _uiState.value = _uiState.value.copy(labelError = "El nombre es obligatorio")
            return false
        }

        val endTime = _uiState.value.selectedEndTime
        val startTime = _uiState.value.selectedStartTime

        if (endTime <= startTime) {
            _uiState.value = _uiState.value.copy(
                timeError = "La hora de fin debe ser posterior a la hora de inicio"
            )
            return false
        }

        return true
    }

    private fun updateDayOfWeek(timestamp: Long) {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            Calendar.SUNDAY -> DayOfWeek.SUNDAY
            else -> DayOfWeek.MONDAY
        }
        _uiState.value = _uiState.value.copy(selectedDayOfWeek = dayOfWeek)
    }
}

data class BlockFormUiState(
    val label: String = "",
    val labelError: String? = null,
    val selectedDate: Long = System.currentTimeMillis(),
    val selectedStartTime: Long = System.currentTimeMillis(),
    val selectedEndTime: Long = System.currentTimeMillis() + 3600000,
    val selectedDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val selectedColor: Int = 0xFF80CBC4.toInt(),
    val isRepetitive: Boolean = false,
    val hasReminder: Boolean = false,
    val timeError: String? = null,
    val isEditMode: Boolean = false
)
