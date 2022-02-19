import 'package:flutter/material.dart';
import 'package:flutter_osm_plugin/flutter_osm_plugin.dart';
import 'package:wheelspuj/uses_map/map.dart'; //Map reference
import 'package:wheelspuj/login/main.dart'; // Import Login page
import 'package:wheelspuj/uses_map/menu/main.dart'; // Import Menu page

TextEditingController username = TextEditingController();
TextEditingController password = TextEditingController();
void changeToMenu(BuildContext context) {
  Navigator.pushNamed(context, '/menu');
}

void forgotPassword(BuildContext context) {
  //print("Forgot password");
}

void register(BuildContext context) {
  //print("Register");
}

MapController mpc = Map.getController();

void main() {
  runApp(
    MaterialApp(
        debugShowCheckedModeBanner: false,
        routes: {
          '/': (context) => Login(
              logInButton: changeToMenu,
              username: username,
              password: password,
              forgotPassword: forgotPassword,
              register: register),
          '/menu': (context) => Menu(
                mpc: mpc,
              )
        },
        initialRoute: '/'),
  );
}
