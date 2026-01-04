package uk.ac.tees.mad.quicklist.notification
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

object NotificationScheduler {

    fun scheduleNotification(
        context: Context,
        taskId: String,
        taskTitle: String,
        taskNotes: String,
        timestamp: Long
    ) {
        val currentTime = System.currentTimeMillis()

        // Don't schedule if time is in the past
        if (timestamp <= currentTime) {
            Log.d("NotificationScheduler", "Time is in the past, not scheduling")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if we can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    context,
                    "Please enable exact alarm permission in settings",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("task_id", taskId)
            putExtra("task_title", taskTitle)
            putExtra("task_notes", taskNotes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Use setExactAndAllowWhileIdle for precise timing even in Doze mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timestamp,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timestamp,
                    pendingIntent
                )
            }

            Log.d("NotificationScheduler", "Notification scheduled for task: $taskTitle at $timestamp")
        } catch (e: SecurityException) {
            Log.e("NotificationScheduler", "Failed to schedule alarm", e)
            Toast.makeText(
                context,
                "Failed to schedule notification. Please check alarm permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun cancelNotification(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TaskNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        Log.d("NotificationScheduler", "Notification cancelled for task: $taskId")
    }
}