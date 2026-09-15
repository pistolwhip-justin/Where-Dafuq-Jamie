package com.pistolwhip.wheredafuqjamie

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object AlarmNotification {
    const val CHANNEL = "jamie_alarm"
    const val ID = 73

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL, "Where Dafuq Jamie!? alarm", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    fun show(context: Context) {
        ensureChannel(context)
        val intent = Intent(context, AlarmActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Where Dafuq Jamie!?")
            .setContentText("Jamie needs to find the phone — swipe to deactivate.")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setFullScreenIntent(pending, true)
            .setOngoing(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(ID, notification)
    }

    fun cancel(context: Context) { context.getSystemService(NotificationManager::class.java).cancel(ID) }
}
