package com.dailyflow.domain.model

data class Category(
    val id: Int = 0,
    val name: String,
    val colorHex: String = "#6200EE",
    val iconName: String = "label",
    val createdAt: Long = System.currentTimeMillis()
)

