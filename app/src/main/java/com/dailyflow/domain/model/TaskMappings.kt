package com.dailyflow.domain.model

import com.dailyflow.data.local.entities.TaskEntity

fun Task.toEntity() = TaskEntity(
    id = id,
    name = name,
    description = description,
    priority = priority.name,
    deadline = deadline,
    status = status.name,
    categoryIds = categoryIds.joinToString(","),
    colorHex = colorHex,
    createdAt = createdAt
)
