package com.dailyflow.data.repository

import android.content.Context
import com.dailyflow.data.local.dao.TaskDao
import com.dailyflow.data.local.entities.toDomain
import com.dailyflow.domain.model.Task
import com.dailyflow.domain.model.toEntity
import com.dailyflow.notifications.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao,
    private val context: Context? = null
) {
    private val notificationScheduler: NotificationScheduler? by lazy {
        context?.let { NotificationScheduler(it) }
    }

    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities -> entities.map { entity -> entity.toDomain() } }

    suspend fun getTaskById(id: Int): Task? =
        taskDao.getTaskById(id)?.toDomain()

    fun getTasksForToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>> =
        taskDao.getTasksForToday(startOfDay, endOfDay).map { entities -> entities.map { entity -> entity.toDomain() } }

    fun getCompletedTasksToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>> =
        taskDao.getCompletedTasksToday(startOfDay, endOfDay).map { entities -> entities.map { entity -> entity.toDomain() } }

    fun getTasksByPriority(priority: String): Flow<List<Task>> =
        taskDao.getTasksByPriority(priority).map { entities -> entities.map { entity -> entity.toDomain() } }

    suspend fun insertTask(task: Task): Long {
        val taskId = taskDao.insertTask(task.toEntity())
        notificationScheduler?.scheduleTaskNotification(task.copy(id = taskId.toInt()))
        return taskId
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
        notificationScheduler?.cancelTaskNotification(task.id)
        if (task.status != com.dailyflow.domain.model.TaskStatus.DONE) {
            notificationScheduler?.scheduleTaskNotification(task)
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
        notificationScheduler?.cancelTaskNotification(task.id)
    }
}
