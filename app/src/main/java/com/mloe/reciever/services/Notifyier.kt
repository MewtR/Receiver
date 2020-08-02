package com.mloe.reciever.services
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mloe.reciever.R
import kotlinx.coroutines.*

private const val TAG = "MyService"
private const val CHANNEL_ID = "channel01"

class Notifyier : Service() {

    private val notificationScope = CoroutineScope(Dispatchers.Main)
    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // start new thread (or kotlin coroutine)
        // In new thread create notif & notify
        // stop
        notificationScope.launch {
            val title: String = intent?.getStringExtra("title") ?: "Hello"
            val text: String = intent?.getStringExtra("text") ?: "It's me again"
            val priority: Int = intent?.getIntExtra("priority", 0) ?: 0
            Log.i(TAG, "title: $title")
            Log.i(TAG, "text: $text")
            Log.i(TAG, "priority $priority")
            createNotificationChannel()
            showNotification(title, text, priority)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    //Works on Android 5.1 API level 22
    private fun createNotification(title: String, text: String, priority: Int): Notification {
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this,  CHANNEL_ID)
            // ic_launcher_foreground does not work as an icon for some reason
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(text)
            .setDefaults(Notification.DEFAULT_ALL) // for some reason this is needed for the
            // notification to be a heads up one on Android below 8.0
            .setPriority(priority)
        return builder.build();
    }

    fun showNotification(title: String, text: String, priority: Int){
        var notification: Notification = createNotification(title, text, priority)
        with(NotificationManagerCompat.from(this)){
            //no idea what to set the notification id as yet so set it to random int
            notify(3, notification)
        }
    }

    //only for android 8.0 and higher (api level 26 and above)
    // creates notification channel with high importance
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.i(TAG, "Creating notification channel")
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH // needs to be high for heads up
            // notification on android 8.0+
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            Log.i(TAG, "Channel importance "+channel.importance)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationScope.cancel()
        Log.i(TAG, "Destroying service")
    }
}

/*
    Priorities:
    High priority: 1
    Low priority: -1
    Default priority: 0
    Max priority: 2
    Min priority: -2
 */

