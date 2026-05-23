package com.dailyflow.domain.model

import com.dailyflow.data.local.entities.BlockEntity

fun Block.toEntity() = BlockEntity(
    id = id,
    label = label,
    dayOfWeek = dayOfWeek.value,
    startTime = startTime,
    durationMinutes = durationMinutes,
    colorHex = colorHex,
    isRepetitive = isRepetitive,
    hasReminder = hasReminder,
    createdAt = createdAt
)
