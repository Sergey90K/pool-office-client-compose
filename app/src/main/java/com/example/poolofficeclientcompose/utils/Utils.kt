package com.example.poolofficeclientcompose.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.poolofficeclientcompose.R

private const val CHANNEL_ID = "POOL_NOTIFICATION"
private const val NOTIFICATION_ID = 1
private const val REQUEST_CODE = 0

fun makeNotification(message: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    val pendingIntent: PendingIntent = createPendingIntent(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_connection_error)
        .setContentTitle(context.getString(R.string.pool_office_client_message))
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@with
        }
        notify(NOTIFICATION_ID, builder.build())
    }
}

fun createPendingIntent(appContext: Context): PendingIntent {
    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
    var peadingIntent = PendingIntent.FLAG_IMMUTABLE
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        peadingIntent = peadingIntent or PendingIntent.FLAG_ONE_SHOT
    }
    return PendingIntent.getActivity(appContext, REQUEST_CODE, intent, peadingIntent)
}