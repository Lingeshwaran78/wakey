import "dart:developer" as dev show log;

import 'package:flutter/services.dart' show MethodChannel;

class Utils {
  static final Utils _utils = Utils._internal();

  factory Utils() {
    return _utils;
  }
  static const bool _showLog = true;
  Utils._internal();

  /// methodChannel Id
  static const methodChannelPlatform = MethodChannel('dev.lingesh.wakey/wake');

  static const isStartedPref = "isStarted";

  ///it can print the longer data
  static void logger(dynamic text) {
    if (_showLog == true) dev.log(text.toString());
  }

  ///it can print the error and its stacktrace used for try catch
  static void loggerStackTrace(dynamic e, StackTrace stackTrace) {
    if (_showLog == true) dev.log('Error: $e ::: stackTrace: $stackTrace');
  }
}
