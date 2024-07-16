package dev.lingesh.wakey

import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val channel = "dev.lingesh.wakey/wake"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                "startWakeService" -> {
                    startWakeService()
                    result.success(null)
                }
                "stopWakeService" -> {
                    stopWakeService()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startWakeService() {
        val intent = Intent(this, WakeService::class.java)
        startService(intent)
    }

    private fun stopWakeService() {
        val intent = Intent(this, WakeService::class.java)
        intent.putExtra("stop", true)
        startService(intent)
    }
}


//import io.flutter.embedding.android.FlutterActivity
//import android.os.PowerManager
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.plugin.common.MethodChannel
//
//class MainActivity: FlutterActivity() {
//    private val channel = "dev.lingesh.wakey/wakelock"
//
//    private var wakeLock: PowerManager.WakeLock? = null
//
//    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//        super.configureFlutterEngine(flutterEngine)
//        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
//            when (call.method) {
//                "enableWakeLock" -> {
//                    enableWakeLock()
//                    result.success(null)
//                }
//                "disableWakeLock" -> {
//                    disableWakeLock()
//                    result.success(null)
//                }
//                else -> {
//                    result.notImplemented()
//                }
//            }
//        }
//    }
//
//    private fun enableWakeLock() {
//        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
//        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::WakeLockTag")
//        wakeLock?.acquire(10*60*1000L /*10 minutes*/)
//    }
//
//    private fun disableWakeLock() {
//        wakeLock?.release()
//    }
//}
