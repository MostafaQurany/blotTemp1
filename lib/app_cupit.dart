import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:untitled4/app_state.dart';

class AppCubitHome extends Cubit<AppStates> {
  AppCubitHome() : super(AppHomeInitionalState());
  static AppCubitHome get(context) => BlocProvider.of(context);



  static const platform = MethodChannel('samples.flutter.dev/battery');
  bool isScanning = false;
  String batteryLevel = 'Unknown battery level.';

  var result;
  // Get battery level.

  Future<void> startScanning()  async {

        platform.invokeMethod('startScan').then((value) {

        });
        batteryLevel = 'Battery level at $result % .';





}
}
