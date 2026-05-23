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

class BlockNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "block_notifications"
        const val NOTIFICATION_ID_BASE = 2000
    }

    override fun onReceive(context: Context, intent: Intent) {
        val blockLabel = intent.getStringExtra(NotificationScheduler.BLOCK_LABEL_EXTRA) ?: "Bloque"
        val blockId = intent.getIntExtra(NotificationScheduler.BLOCK_ID_EXTRA, 0)

        createNotificationChannel(context)

        // Crear intent para abrir la app en BlockForm
        val pendingIntent = PendingIntent.getActivity(
            context,
            blockId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "block_form")
                putExtra("block_id", blockId)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cat_mascot)
            .setContentTitle("¡Bloque próximo! ⏰")
            .setContentText("El bloque '$blockLabel' comenzará en 5 minutos")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_BASE + blockId,
                notification
            )
        } catch (e: SecurityException) {
            // El usuario no ha concedido permiso de notificaciones
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bloques de tiempo"
            val descriptionText = "Recordatorios de bloques próximos a comenzar"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
