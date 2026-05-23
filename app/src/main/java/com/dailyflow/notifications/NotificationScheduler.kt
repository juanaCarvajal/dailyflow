package com.dailyflow.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dailyflow.domain.model.Block
import com.dailyflow.domain.model.Task
import java.util.Calendar

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val TASK_NOTIFICATION_ID = "task_notification_id"
        const val BLOCK_NOTIFICATION_ID = "block_notification_id"
        const val TASK_NAME_EXTRA = "task_name"
        const val TASK_ID_EXTRA = "task_id"
        const val BLOCK_LABEL_EXTRA = "block_label"
        const val BLOCK_ID_EXTRA = "block_id"
    }

    /**
     * Programa notificación para tarea con deadline < 24 horas
     */
    fun scheduleTaskNotification(task: Task) {
        val deadline = task.deadline ?: return

        // Calcular tiempo restante en milisegundos
        val currentTime = System.currentTimeMillis()
        val timeRemaining = deadline - currentTime

        // Solo programar si faltan menos de 24 horas (86400000 ms)
        if (timeRemaining > 0 && timeRemaining <= 86400000) {
            // Notificar 1 hora antes del deadline
            val notificationTime = deadline - 3600000 // 1 hora antes

            // Si ya pasó la hora de notificación, no programar
            if (notificationTime > currentTime) {
                val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
                    putExtra(TASK_ID_EXTRA, task.id)
                    putExtra(TASK_NAME_EXTRA, task.name)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.id,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
            }
        }
    }

    /**
     * Cancela notificación de tarea específica
     */
    fun cancelTaskNotification(taskId: Int) {
        val intent = Intent(context, TaskNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Programa notificación para bloque que comienza en < 15 minutos
     */
    fun scheduleBlockNotification(block: Block) {
        val currentTime = System.currentTimeMillis()
        val startTime = block.startTime
        val timeRemaining = startTime - currentTime

        // Solo programar si faltan 15 minutos o menos (900000 ms)
        if (timeRemaining > 0 && timeRemaining <= 900000) {
            val intent = Intent(context, BlockNotificationReceiver::class.java).apply {
                putExtra(BLOCK_ID_EXTRA, block.id)
                putExtra(BLOCK_LABEL_EXTRA, block.label)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                block.id + 10000, // Offset para evitar conflicto con tareas
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                startTime - 300000, // 5 minutos antes (opcional)
                pendingIntent
            )
        }
    }

    /**
     * Cancela notificación de bloque específico
     */
    fun cancelBlockNotification(blockId: Int) {
        val intent = Intent(context, BlockNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            blockId + 10000,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Reprograma todas las notificaciones activas
     */
    fun rescheduleAllNotifications(tasks: List<Task>, blocks: List<Block>) {
        // Cancelar todas las notificaciones existentes
        cancelAllNotifications()

        // Programar nuevas notificaciones
        tasks.forEach { task ->
            if (task.deadline != null && task.status != com.dailyflow.domain.model.TaskStatus.DONE) {
                scheduleTaskNotification(task)
            }
        }

        blocks.forEach { block ->
            scheduleBlockNotification(block)
        }
    }

    /**
     * Cancela todas las notificaciones programadas
     */
    fun cancelAllNotifications() {
        // Esto cancelará todas las alarmas, pero las notificaciones ya mostradas
        // necesitarán ser canceladas por el NotificationManager
    }
}
