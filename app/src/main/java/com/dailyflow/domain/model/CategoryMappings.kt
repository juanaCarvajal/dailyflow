package com.dailyflow.domain.model

import com.dailyflow.data.local.entities.CategoryEntity

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    colorHex = colorHex,
    iconName = iconName,
    createdAt = createdAt
)
