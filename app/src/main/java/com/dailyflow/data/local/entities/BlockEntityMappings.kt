package com.dailyflow.data.local.entities

import com.dailyflow.domain.model.Block
import com.dailyflow.domain.model.DayOfWeek

fun BlockEntity.toDomain() = Block(
    id = id,
    label = label,
    dayOfWeek = DayOfWeek.entries.first { it.value == dayOfWeek },
    startTime = startTime,
    durationMinutes = durationMinutes,
    colorHex = colorHex,
    isRepetitive = isRepetitive,
    hasReminder = hasReminder,
    createdAt = createdAt
)
