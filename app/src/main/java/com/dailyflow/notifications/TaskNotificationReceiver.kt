package com.dailyflow.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dailyflow.MainActivity
import com.dailyflow.R
import com.dailyflow.ui.navigation.AppNavGraph
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "task_notifications"
        const val NOTIFICATION_ID_BASE = 1000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra(NotificationScheduler.TASK_NAME_EXTRA) ?: "Tarea"
        val taskId = intent.getIntExtra(NotificationScheduler.TASK_ID_EXTRA, 0)

        createNotificationChannel(context)

        // Crear intent para abrir la app en TaskForm
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "task_form")
                putExtra("task_id", taskId)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cat_mascot)
            .setContentTitle("¡Tarea pendiente! ⏰")
            .setContentText("La tarea '$taskName' está próxima a vencer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_BASE + taskId,
                notification
            )
        } catch (e: SecurityException) {
            // El usuario no ha concedido permiso de notificaciones
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Tareas"
            val descriptionText = "Recordatorios de tareas próximas a vencer"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
