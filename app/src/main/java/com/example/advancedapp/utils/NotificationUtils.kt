package com.example.advancedapp.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.advancedapp.DetailActivity
import com.example.advancedapp.MainActivity
import com.example.advancedapp.R

object NotificationUtils {

    private const val CHANNEL_ID = "channelId"
    private const val CHANNEL_NAME = "Loading App"
    private const val REQUEST_CODE_LoadApp = 1000

    //I make first, data class of channel details and i will put the data of channel details here :
    @SuppressLint("InlinedApi")
    fun getFilesChannel(context: Context): ChannelDetails {
        return ChannelDetails(
            CHANNEL_ID,
            CHANNEL_NAME,
            context.getString(R.string.app_description),
            NotificationManager.IMPORTANCE_LOW,
            NotificationCompat.PRIORITY_HIGH,
            NotificationCompat.VISIBILITY_PUBLIC
        )
    }

    //Here i will make a notification channel :
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, channelDetails: ChannelDetails) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationChannel(
            channelDetails.id,
            channelDetails.name,
            channelDetails.importance
        ).apply {
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
            lightColor = context.getColor(R.color.teal_200)
            description = channelDetails.description
            lockscreenVisibility = channelDetails.visibility
            notificationManager.createNotificationChannel(this)
        }
    }

    //Fun to send a notification with all properties that you want , note : i will use this function in Main Activity to pass all data i want , i wait it to come there :)
    fun sendNotification(
        context: Context,
        titleId: String,
        stateId: String,
        textNotify: String,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifyIntent = Intent(context, DetailActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        notifyIntent.putExtras(
            DetailActivity.withExtras(
                title = titleId,
                state = stateId
            )
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_LoadApp,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val loadAppChannel = getFilesChannel(context)

        val notification = NotificationCompat.Builder(context, loadAppChannel.id)
            .setContentTitle(titleId)
            .setContentText(textNotify)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.colorAccent))
            .setLights(ContextCompat.getColor(context, R.color.colorAccent), 1000, 3000)
            .setVisibility(loadAppChannel.visibility)
            .setPriority(loadAppChannel.priority)
            .addAction(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.check_status),
                    pendingIntent
                )
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }
}