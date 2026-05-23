package com.dailyflow.domain.model

enum class Priority { HIGH, MEDIUM, LOW }
enum class TaskStatus { PENDING, IN_PROGRESS, DONE }

data class Task(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val priority: Priority,
    val deadline: Long?,
    val status: TaskStatus,
    val categoryIds: List<Int> = emptyList(),
    val colorHex: String = "#F06292",
    val createdAt: Long = System.currentTimeMillis()
)

