package com.dailyflow.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val priority: String, // "HIGH" | "MEDIUM" | "LOW"
    val deadline: Long?,
    val status: String = "PENDING", // "PENDING" | "IN_PROGRESS" | "DONE"
    val categoryIds: String = "", // IDs separados por coma
    val colorHex: String = "#F06292",
    val createdAt: Long = System.currentTimeMillis()
)
