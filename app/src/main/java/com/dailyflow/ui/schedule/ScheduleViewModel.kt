package com.dailyflow.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyflow.data.repository.BlockRepository
import com.dailyflow.domain.model.Block
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleViewModel(
    private val blockRepository: BlockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadBlocks()
        initializeCurrentWeek()
    }

    private fun loadBlocks() {
        viewModelScope.launch {
            blockRepository.getAllBlocks().collect { blocks ->
                _uiState.value = _uiState.value.copy(
                    allBlocks = blocks
                )
                filterBlocksForSelectedDay()
            }
        }
    }

    private fun initializeCurrentWeek() {
        val calendar = Calendar.getInstance()

        // Calcular el lunes de la semana actual
        val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = when (todayDayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        // Retroceder al lunes de esta semana
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday)
        val weekStart = calendar.timeInMillis

        // Calcular el día de semana actual (1=Lunes ... 7=Domingo)
        val todayCalendar = Calendar.getInstance()
        val currentDayOfWeek = when (todayCalendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }

        _uiState.value = _uiState.value.copy(
            currentWeekStart = weekStart,
            selectedDayOfWeek = currentDayOfWeek
        )

        filterBlocksForSelectedDay()
    }

    fun onDaySelected(dayOfWeek: Int) {
        _uiState.value = _uiState.value.copy(
            selectedDayOfWeek = dayOfWeek
        )
        filterBlocksForSelectedDay()
    }

    private fun filterBlocksForSelectedDay() {
        val filteredBlocks = _uiState.value.allBlocks
            .filter { block ->
                // ✅ CORREGIDO: Mostrar bloques repetitivos todos los días
                block.isRepetitive || block.dayOfWeek.value == _uiState.value.selectedDayOfWeek
            }
            .sortedBy { it.startTime }

        _uiState.value = _uiState.value.copy(
            blocksForSelectedDay = filteredBlocks
        )
    }

    fun onPreviousWeek() {
        val newWeekStart = _uiState.value.currentWeekStart - (7 * 24 * 60 * 60 * 1000)
        _uiState.value = _uiState.value.copy(
            currentWeekStart = newWeekStart
        )
    }

    fun onNextWeek() {
        val newWeekStart = _uiState.value.currentWeekStart + (7 * 24 * 60 * 60 * 1000)
        _uiState.value = _uiState.value.copy(
            currentWeekStart = newWeekStart
        )
    }

    fun onDeleteBlock(block: Block) {
        viewModelScope.launch {
            blockRepository.deleteBlock(block)
        }
    }
}

data class ScheduleUiState(
    val allBlocks: List<Block> = emptyList(),
    val blocksForSelectedDay: List<Block> = emptyList(),
    val selectedDayOfWeek: Int = 1, // 1=Lunes ... 7=Domingo
    val currentWeekStart: Long = System.currentTimeMillis()
)
