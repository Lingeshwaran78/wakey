import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:wakey/utils/utils.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Permission.notification.request();
  runApp(
    const MaterialApp(
      title: "Wakey",
      home: MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isStarted = false;
  @override
  void initState() {
    WidgetsBinding.instance.addPostFrameCallback(
      (timeStamp) => _init(),
    );
    super.initState();
  }

  Future _init() async {
    final pref = await SharedPreferences.getInstance();
    setState(
      () => isStarted = pref.getBool(Utils.isStartedPref) ?? false,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Wake Service Example'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            if (isStarted == false)
              ElevatedButton(
                onPressed: () => startWakeService(true),
                child: const Text('Start Wake Service'),
              )
            else
              ElevatedButton(
                onPressed: stopWakeService,
                child: const Text('Stop Wake Service'),
              ),
          ],
        ),
      ),
    );
  }

  void startWakeService(bool useAlternativeMethod) async {
    try {
      await Utils.methodChannelPlatform.invokeMethod(
          'startService', {'alternativeMethod': useAlternativeMethod}).then(
        (value) async {
          Utils.logger("Result of Start Service from method channel :: $value");
          if (value != null) {
            final pref = await SharedPreferences.getInstance();
            pref.setBool(Utils.isStartedPref, true);
            setState(() => isStarted = true);
          }
        },
      );
    } on PlatformException catch (e) {
      Utils.logger("Failed to start service: '${e.message}'.");
    } catch (e, stackTrace) {
      Utils.loggerStackTrace(e, stackTrace);
    }
  }

  void stopWakeService() async {
    try {
      await Utils.methodChannelPlatform.invokeMethod('stopService').then(
        (value) async {
          Utils.logger("Result of stop Service from method channel :: $value");
          if (value != null) {
            final pref = await SharedPreferences.getInstance();
            pref.setBool(Utils.isStartedPref, false);
            setState(() => isStarted = false);
          }
        },
      );
    } on PlatformException catch (e) {
      Utils.logger("Failed to stop service: '${e.message}'.");
    } catch (e, stackTrace) {
      Utils.loggerStackTrace(e, stackTrace);
    }
  }
}

// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';
// import 'package:flutter_local_notifications/flutter_local_notifications.dart';
//
// void main() {
//   runApp(MyApp());
// }
//
// class MyApp extends StatelessWidget {
//   static const platform = MethodChannel('dev.lingesh.wakey/wake');
//   final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
//       FlutterLocalNotificationsPlugin();
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       home: Scaffold(
//         appBar: AppBar(
//           title: const Text('Wake Service Example'),
//         ),
//         body: Center(
//           child: Column(
//             mainAxisAlignment: MainAxisAlignment.center,
//             children: <Widget>[
//               ElevatedButton(
//                 onPressed: () => startWakeService(true),
//                 child: const Text('Start Wake Service'),
//               ),
//               ElevatedButton(
//                 onPressed: stopWakeService,
//                 child: const Text('Stop Wake Service'),
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
//
//   void startWakeService(bool useAlternativeMethod) async {
//     try {
//       await platform.invokeMethod(
//           'startService', {'alternativeMethod': useAlternativeMethod});
//       showNotification();
//     } on PlatformException catch (e) {
//       print("Failed to start service: '${e.message}'.");
//     }
//   }
//
//   void stopWakeService() async {
//     try {
//       await platform.invokeMethod('stopService');
//       flutterLocalNotificationsPlugin.cancelAll();
//     } on PlatformException catch (e) {
//       print("Failed to stop service: '${e.message}'.");
//     }
//   }
//
//   void showNotification() async {
//     const AndroidNotificationDetails androidPlatformChannelSpecifics =
//         AndroidNotificationDetails(
//       'foreground_service_channel',
//       'Foreground Service',
//       channelDescription:
//           'This notification appears when the foreground service is running.',
//       importance: Importance.max,
//       priority: Priority.high,
//       ongoing: true,
//     );
//     const NotificationDetails platformChannelSpecifics =
//         NotificationDetails(android: androidPlatformChannelSpecifics);
//     await flutterLocalNotificationsPlugin.show(
//       0,
//       'Wake Service',
//       'Preventing device from sleeping',
//       platformChannelSpecifics,
//     );
//   }
// }
