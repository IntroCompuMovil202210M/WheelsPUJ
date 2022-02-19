import 'package:flutter/material.dart';
import 'package:wheelspuj/login/main.dart'; // Import Login page

void main() {
  runApp(
    MaterialApp(
        debugShowCheckedModeBanner: false,
        home: Login(logInButton: () {})),
  );
}
