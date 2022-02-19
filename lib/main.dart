import 'package:flutter/material.dart';
import 'package:wheelspuj/login/main.dart'; // Import Login page

TextEditingController username = TextEditingController();
TextEditingController password = TextEditingController();
void changeToMenu() {
  //print("User: " + username.text + " password: " + password.text);
}

void forgotPassword() {
  //print("Forgot password");
}

void register() {
  //print("Register");
}

void main() {
  runApp(
    MaterialApp(
        debugShowCheckedModeBanner: false,
        home: Login(
          forgotPassword: forgotPassword,
          logInButton: changeToMenu,
          register: register,
          password: password,
          username: username,
        )),
  );
}
