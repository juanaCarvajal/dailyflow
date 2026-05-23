package com.dailyflow.data.local.entities

import com.dailyflow.domain.model.Category

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    createdAt = createdAt
)
