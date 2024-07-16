package dev.lingesh.wakey

import android.app.Service
import android.content.Intent
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        if (debug) {
            Log.d(tag, "Service creating")
        }
        pm = getSystemService(POWER_SERVICE) as PowerManager
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTag::WakeLock")
        wl.acquire(10*60*1000L /*10 minutes*/)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || !intent.getBooleanExtra("stop", false)) {
            alternativeMethod = intent?.getBooleanExtra("alternativeMethod", false) ?: false
            if (alternativeMethod) {
                try {
                    val timeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                    getSharedPreferences(packageName, 0).edit().putInt(timeoutPref, timeout).apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE)
            }
            return START_STICKY
        }
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (debug) {
            Log.d(tag, "Service destroying")
        }
        wl.release()
        if (alternativeMethod) {
            val timeout = getSharedPreferences(packageName, 0).getInt(timeoutPref, 0)
            if (timeout > 0) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, timeout)
            }
        }
    }
}

//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Intent
//import android.os.Build
//import android.os.IBinder
//import android.os.PowerManager
//import android.provider.Settings
//import android.util.Log
//import androidx.core.app.NotificationCompat
//
//class WakeService : Service() {
//    private val TAG = "DoNotSleepWakeService"
//    private val TIMEOUT_PREF = "SCREEN_OFF_TIMEOUT"
//    private val DEBUG = true
//    private var alternativeMethod = false
//    private lateinit var pm: PowerManager
//    private lateinit var wl: PowerManager.WakeLock
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        if (DEBUG) {
//            Log.d(TAG, "Service creating")
//        }
//        pm = getSystemService(POWER_SERVICE) as PowerManager
//        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyTag::WakeLock")
//        wl.acquire()
//        startForeground(R.string.service_running_content, createNotification())
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if (intent == null || !intent.getBooleanExtra("stop", false)) {
//            alternativeMethod = intent?.getBooleanExtra("alternativeMethod", false) ?: false
//            if (alternativeMethod) {
//                try {
//                    val timeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
//                    getSharedPreferences(packageName, 0).edit().putInt(TIMEOUT_PREF, timeout).apply()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE)
//            }
//            return START_STICKY
//        }
//        stopSelf()
//        return START_NOT_STICKY
//    }
//
//    override fun onDestroy() {
//        if (DEBUG) {
//            Log.d(TAG, "Service destroying")
//        }
//        wl.release()
//        if (alternativeMethod) {
//            val timeout = getSharedPreferences(packageName, 0).getInt(TIMEOUT_PREF, 0)
//            if (timeout > 0) {
//                Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, timeout)
//            }
//        }
//    }
//
//    private fun createNotification(): Notification {
//        val intent = Intent(applicationContext, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        val stopIntent = Intent(applicationContext, WakeService::class.java).apply {
//            putExtra("stop", true)
//        }
//        val stopPendingIntent = PendingIntent.getService(applicationContext, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
//
//        val notificationBuilder = NotificationCompat.Builder(this, "ServiceChannel").apply {
//            setSmallIcon(R.drawable.ic_stat_name)
//            setContentTitle(getString(R.string.content_title))
//            setContentText(getString(R.string.content_text))
//            setTicker(getString(R.string.content_text))
//            setOngoing(true)
//            setContentIntent(pendingIntent)
//            addAction(R.drawable.ic_stop, getString(R.string.stop_service), stopPendingIntent)
//            color = getColor(R.color.colorPrimary)
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            val channel = NotificationChannel("ServiceChannel", "Service", NotificationManager.IMPORTANCE_LOW).apply {
//                description = "Service background notification"
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        return notificationBuilder.build()
//    }
//}
