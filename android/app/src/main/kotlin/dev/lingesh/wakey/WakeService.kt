package dev.lingesh.wakey

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log

class WakeService : Service() {
    private val tag = "WakeyWakeService"
    private val timeoutPref = "SCREEN_OFF_TIMEOUT"
    private val debug = true
    private var alternativeMethod = false
    private lateinit var pm: PowerManager
    private lateinit var wl: PowerManager.WakeLock
    private lateinit var sharedPreferences: SharedPreferences

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        if (debug) {
            Log.d(tag, "Service creating")
        }

        pm = getSystemService(POWER_SERVICE) as PowerManager
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTag::WakeLock")
        wl.acquire(10 * 60 * 1000L) // 10 minutes
        sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)

        // Create a notification channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "foreground_service_channel"
            val channelName = "Foreground Service Channel"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || !intent.getBooleanExtra("stop", false)) {
            alternativeMethod = intent?.getBooleanExtra("alternativeMethod", false) ?: false

            if (alternativeMethod) {
                try {
                    if (!Settings.System.canWrite(this)) {
                        val settingsIntent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                        settingsIntent.data = Uri.parse("package:$packageName")
                        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(settingsIntent)
                    } else {
                        try {
                            val timeout = Settings.System.getInt(
                                contentResolver,
                                Settings.System.SCREEN_OFF_TIMEOUT
                            )
                            sharedPreferences.edit().putInt(timeoutPref, timeout).apply()
                            Settings.System.putInt(
                                contentResolver,
                                Settings.System.SCREEN_OFF_TIMEOUT,
                                Integer.MAX_VALUE
                            )
                        } catch (e: Exception) {
                            Log.e(tag, "Error modifying system settings: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error modifying system settings: ${e.message}")
                }
            }

            // Create a notification with a PendingIntent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "foreground_service_channel"
                val channelName = "Foreground Service Channel"
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
                val manager = getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)

                // Intent to launch the app
                val notificationIntent = Intent(this, MainActivity::class.java)
                notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val notification = Notification.Builder(this, channelId)
                    .setContentTitle("Wake Service")
                    .setContentText("Service is running...")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentIntent(pendingIntent) // Set the PendingIntent
                    .setAutoCancel(false) // Dismiss the notification when tapped if true
                    .build()

            startForeground(1, notification)
            return START_STICKY
        }
     }

        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        if (debug) {
            Log.d(tag, "Service destroying")
        }

        wl.release()

        if (alternativeMethod) {
            try {
                val timeout = sharedPreferences.getInt(timeoutPref, 0)
                if (timeout > 0) {
                    if (Settings.System.canWrite(this)) {
                        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, timeout)
                    } else {
                        Log.e(tag, "Permission to write system settings is not granted")
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error restoring system settings: ${e.message}")
            }
        }
    }
}
