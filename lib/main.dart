// import 'package:components/components.dart';
// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';
//
// void main() {
//   WidgetsFlutterBinding.ensureInitialized();
//   runApp(const MyApp());
// }
//
// class MyApp extends StatelessWidget {
//   const MyApp({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       theme: ThemeData(
//         scaffoldBackgroundColor: Colors.black,
//         appBarTheme: const AppBarTheme(color: Colors.yellow),
//       ),
//       debugShowCheckedModeBanner: false,
//       home: const HomePage(),
//     );
//   }
// }
//
// class HomePage extends StatefulWidget {
//   const HomePage({super.key});
//
//   @override
//   State<HomePage> createState() => _HomePageState();
// }
//
// class _HomePageState extends State<HomePage> {
//   int counter = 0;
//
//   static const platform = MethodChannel('dev.lingesh.wakey/wakelock');
//   bool _isWakeLockEnabled = false;
//
//   Future<void> _enableWakeLock() async {
//     try {
//       await platform.invokeMethod('enableWakeLock');
//       setState(() {
//         _isWakeLockEnabled = true;
//       });
//     } on PlatformException catch (e) {
//       print("Failed to enable wake lock: '${e.message}'.");
//     }
//   }
//
//   Future<void> _disableWakeLock() async {
//     try {
//       await platform.invokeMethod('disableWakeLock');
//       setState(() {
//         _isWakeLockEnabled = false;
//       });
//     } on PlatformException catch (e) {
//       print("Failed to disable wake lock: '${e.message}'.");
//     }
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: const Text('Hello World'),
//         actions: [
//           ElevatedButton(
//             onPressed: () => Components.showLogging(false),
//             child: const Text("toggle"),
//           )
//         ],
//       ),
//       body: Center(
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.center,
//           children: [
//             Text(
//               'Wake Lock is ${_isWakeLockEnabled ? 'Enabled' : 'Disabled'}',
//             ),
//             const SizedBox(height: 20),
//             ElevatedButton(
//               onPressed:
//                   _isWakeLockEnabled ? _disableWakeLock : _enableWakeLock,
//               child: Text(_isWakeLockEnabled
//                   ? 'Disable Wake Lock'
//                   : 'Enable Wake Lock'),
//             ),
//           ],
//         ),
//       ),
//       // floatingActionButton: FloatingActionButton(
//       //   onPressed: () async => await Permission.systemSettings.request(),
//       // ),
//     );
//   }
// }
//
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
//     <uses-permission android:name="android.permission.WAKE_LOCK"/>
//     <uses-permission android:name="android.permission.DISABLE_KEYGUARD"/>
//     <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
//     <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
//     <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
///
// import 'package:flutter/material.dart';
// import 'package:permission_handler/permission_handler.dart';
// import 'package:android_intent_plus/android_intent.dart';
// import 'package:android_intent_plus/flag.dart';
//
// void main() {
//   runApp(const MyApp());
// }
//
// class MyApp extends StatelessWidget {
//   const MyApp({super.key});
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       home: Scaffold(
//         appBar: AppBar(
//           title: const Text('Modify System Settings Permission'),
//         ),
//         body: Center(
//           child: ElevatedButton(
//             onPressed: () {
//               checkAndRequestWriteSettingsPermission();
//             },
//             child: const Text('Request Modify System Settings Permission'),
//           ),
//         ),
//       ),
//     );
//   }
//
//   Future<void> checkAndRequestWriteSettingsPermission() async {
//     if (!await Permission.systemAlertWindow.isGranted) {
//       const intent = AndroidIntent(
//         action: 'android.settings.action.MANAGE_WRITE_SETTINGS',
//         flags: <int>[Flag.FLAG_ACTIVITY_NEW_TASK],
//         data: 'package:dev.lingesh.wakey',
//       );
//       await intent.launch();
//     }
//   }
// }
///
import 'package:components/components.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initNotifications();
  runApp(MyApp());
}

Future<void> initNotifications() async {
  const AndroidInitializationSettings initializationSettingsAndroid =
      AndroidInitializationSettings('app_icon');
  const InitializationSettings initializationSettings =
      InitializationSettings(android: initializationSettingsAndroid);
  await FlutterLocalNotificationsPlugin().initialize(initializationSettings);
}

class MyApp extends StatelessWidget {
  static const platform = MethodChannel('dev.lingesh.wakey/wake');

  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Prevent Sleep Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              ElevatedButton(
                onPressed: () {
                  startWakeService();
                },
                child: const Text('Start Service'),
              ),
              ElevatedButton(
                onPressed: () {
                  stopWakeService();
                },
                child: const Text('Stop Service'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> startWakeService() async {
    try {
      await platform.invokeMethod('startWakeService');
      await showNotification('Service Running', 'The wake service is active.');
    } on PlatformException catch (e, stackTrace) {
      Components.loggerStackTrace(e, stackTrace);
      // print("Failed to start wake service: '${e.message}'.");
    }
  }

  Future<void> stopWakeService() async {
    try {
      await platform.invokeMethod('stopWakeService');
      await cancelNotification();
    } on PlatformException catch (e, stackTrace) {
      Components.loggerStackTrace(e, stackTrace);
      // print("Failed to stop wake service: '${e.message}'.");
    }
  }

  Future<void> showNotification(String title, String body) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails('your_channel_id', 'your_channel_name',
            channelDescription: 'your_channel_description',
            importance: Importance.defaultImportance,
            priority: Priority.defaultPriority,
            ticker: 'ticker');
    const NotificationDetails platformChannelSpecifics =
        NotificationDetails(android: androidPlatformChannelSpecifics);
    await FlutterLocalNotificationsPlugin()
        .show(0, title, body, platformChannelSpecifics, payload: 'item x');
  }

  Future<void> cancelNotification() async {
    await FlutterLocalNotificationsPlugin().cancel(0);
  }
}
