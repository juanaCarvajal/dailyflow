package com.dailyflow.data.local.entities

import com.dailyflow.domain.model.Task
import com.dailyflow.domain.model.Priority
import com.dailyflow.domain.model.TaskStatus

fun TaskEntity.toDomain() = Task(
    id = id,
    name = name,
    description = description,
    priority = Priority.valueOf(priority),
    deadline = deadline,
    status = TaskStatus.valueOf(status),
    categoryIds = if (categoryIds.isBlank()) emptyList()
                    else categoryIds.split(",").map(String::toInt),
    colorHex = colorHex,
    createdAt = createdAt
)
