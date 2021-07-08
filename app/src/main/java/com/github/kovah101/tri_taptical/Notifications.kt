package com.github.kovah101.tri_taptical

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService

class Notifications() {
    var notificationID = 0
    val channelID = "TTT_Invite"
    private val TAG = "NOTIFICATION-TEST"

    fun createChannel(context: Context){
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val name = "TTT Invite"
                val descriptionText = "Tri-Tac-Toe Invite"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification Channel Created")

            }
        }


    fun Notify(context: Context, hostName: String, number: Int, playerNumber: Int, myUsername: String, myEmail: String) {
        // intent notification will send recipient to online lobby
        val onlineLobby = Intent(context, OnlineLobby::class.java)
        // store source of intent (notification) and the player data to populate opened lobby
        onlineLobby.putExtra("Notification", true)
        onlineLobby.putExtra("playerNumber", playerNumber)
        onlineLobby.putExtra("userName", myUsername)
        onlineLobby.putExtra("email", myEmail)
        // need Task Stack Builder to bypass Login screen
        val bypassLogin: PendingIntent? = TaskStackBuilder.create(context).run {
            // add intent that inflates the back stack
            addNextIntentWithParentStack(onlineLobby)
            // get pending intent with entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val NOTIFYTAG = "new request"
        // increment notification ID to keep unique
        notificationID ++
        var playerColor = 0
        when (playerNumber) {
            1 -> playerColor = R.color.playerOne
            2 -> playerColor = R.color.playerTwo
            3 -> playerColor = R.color.playerThree
            4 -> playerColor = R.color.playerFour
        }
        // play around with colours & sounds
        // player number dependant
        val builder = NotificationCompat.Builder(context, channelID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setColor(playerColor)
            .setContentTitle("Tri-Tac-Toe Invite!")
            .setContentText("$hostName invited you - You are P:$playerNumber")
            .setNumber(number)
            .setSmallIcon(R.drawable.tri_tac_toe_icon)
            .setContentIntent(bypassLogin)
            .setAutoCancel(true)

        // notification manager
        val notManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notManager.notify(NOTIFYTAG, notificationID, builder.build())
        Log.d(TAG, "Inside notify function")
    }
}