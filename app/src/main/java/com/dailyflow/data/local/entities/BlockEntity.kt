package com.dailyflow.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val label: String,
    val dayOfWeek: Int, // 1=Lunes ... 7=Domingo
    val startTime: Long,
    val durationMinutes: Int,
    val colorHex: String = "#80CBC4",
    val isRepetitive: Boolean = false,
    val hasReminder: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
