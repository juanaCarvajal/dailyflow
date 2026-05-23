package com.dailyflow.domain.model

enum class DayOfWeek(val value: Int, val displayName: String) {
    MONDAY(1, "Lun"),
    TUESDAY(2, "Mar"),
    WEDNESDAY(3, "Mié"),
    THURSDAY(4, "Jue"),
    FRIDAY(5, "Vie"),
    SATURDAY(6, "Sáb"),
    SUNDAY(7, "Dom")
}

data class Block(
    val id: Int = 0,
    val label: String,
    val dayOfWeek: DayOfWeek,
    val startTime: Long,
    val durationMinutes: Int,
    val colorHex: String = "#80CBC4",
    val isRepetitive: Boolean = false,
    val hasReminder: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

