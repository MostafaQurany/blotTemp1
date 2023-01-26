import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:untitled4/app_cupit.dart';
import 'package:untitled4/app_state.dart';

class Home extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => AppCubitHome(),
      child: BlocConsumer<AppCubitHome,AppStates>(
          listener: (context, state) {

          },
          builder: (context, state) {
            var cubit = AppCubitHome.get(context);
            return Scaffold(
              appBar: AppBar(),
              body: Column(
                children: [

                ],
              ),
              floatingActionButton: FloatingActionButton(onPressed: () {
                cubit.startScanning();
              },
                  child: Icon(Icons.search)),
            );
          }
      ),
    );
}
}
