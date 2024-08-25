package dev.lingesh.wakey

import android.content.Intent
import android.os.Build
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val channel = "dev.lingesh.wakey/wake"
    override fun configureFlutterEngine( flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                "startService" -> {
                    try{
                        val intent = Intent(this, WakeService::class.java)
                        intent.putExtra(
                            "alternativeMethod",
                            call.argument<Boolean>("alternativeMethod")
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                        result.success(true)
                    } catch (error:Exception){
                        result.success(false)
                    }
                }
                "stopService" -> {

                    try {
                        val intent = Intent(this, WakeService::class.java)
                        intent.putExtra("stop", true)
                        stopService(intent)
                        result.success(true)
                    } catch (error:Exception){
                        result.success(false)

                    }

                }
                else -> result.notImplemented()
            }
        }
    }
}
