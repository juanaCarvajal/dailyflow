package com.dailyflow

import android.app.Application
import com.dailyflow.data.local.AppDatabase
import com.dailyflow.data.repository.BlockRepository
import com.dailyflow.data.repository.CategoryRepository
import com.dailyflow.data.repository.TaskRepository

class DailyFlowApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val taskRepository by lazy { TaskRepository(database.taskDao(), this) }
    val blockRepository by lazy { BlockRepository(database.blockDao(), this) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
}
