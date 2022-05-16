// ignore_for_file: use_build_context_synchronously

import 'dart:io';
import 'package:cloud_firestore/cloud_firestore.dart'; // for Cloud Firestore
import 'package:firebase_storage/firebase_storage.dart'; // for Firebase Storage
import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart'; // for Firebase
import 'package:firebase_auth/firebase_auth.dart'; // for FirebaseAuth
import 'package:image_picker/image_picker.dart'; // for ImagePicker
import 'package:wheelspuj/firebase_options.dart'; // Firebase Options
import 'package:wheelspuj/login/login.dart'; // Login page
import 'package:bot_toast/bot_toast.dart'; // for Toast
import 'package:wheelspuj/login/register.dart'; // Register Screens

TextEditingController name = TextEditingController();
TextEditingController surname = TextEditingController();
TextEditingController username = TextEditingController();
TextEditingController password = TextEditingController();
TextEditingController confirmPassword = TextEditingController();
ImagePicker? picker;
XFile? image;
XFile? carImage;

void goToMap(BuildContext context) {
  Navigator.pushNamed(context, '/map');
}

void showToast(String msg, {Color? background, Color? textColor}) {
  if (background != null) {
    if (textColor != null) {
      BotToast.showText(
          text: msg, contentColor: textColor, backgroundColor: background);
    } else {
      BotToast.showText(text: msg, backgroundColor: background);
    }
  } else if (textColor != null) {
    BotToast.showText(text: msg, contentColor: textColor);
  } else {
    BotToast.showText(text: msg);
  }
}

void logInFunction(BuildContext context) async {
  showToast("Trying log in...");
  try {
    await FirebaseAuth.instance.signInWithEmailAndPassword(
        email: username.text, password: password.text);
    if (FirebaseAuth.instance.currentUser != null) {
      showToast("Ok",
          background: const Color.fromARGB(255, 0, 240, 8),
          textColor: const Color.fromARGB(255, 0, 55, 20));
      goToMap(context);
    }
  } on FirebaseAuthException catch (e) {
    if (e.code == 'user-not-found') {
      showToast('No user found for the email ${username.text}');
    } else if (e.code == 'wrong-password') {
      showToast(
          'Wrong password provided for the user with email ${username.text}');
    }
  }
}

void forgotPasswordFunction(BuildContext context) {
  print("Forgot password");
}

Future<bool> registerFunction() async {
  try {
    await FirebaseAuth.instance.createUserWithEmailAndPassword(
      email: username.text,
      password: password.text,
    );
    return true;
  } on FirebaseAuthException catch (e) {
    if (e.code == 'weak-password') {
      showToast('The password provided is too weak');
    } else if (e.code == 'email-already-in-use') {
      showToast('The account already exists for the email ${username.text}');
    }
  } catch (e) {
    showToast(e.toString());
  }
  return false;
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  picker = ImagePicker();
  runApp(
    const App(),
  );
}

class App extends StatelessWidget {
  const App({
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        debugShowCheckedModeBanner: false,
        builder: BotToastInit(),
        navigatorObservers: [BotToastNavigatorObserver()],
        routes: {
          '/': (context) => Login(
              logInFunction: logInFunction,
              username: username,
              password: password,
              forgotPasswordFunction: forgotPasswordFunction,
              registerScreen: registerScreen),
          '/register': (context) => Register(
              registerFunctionPassenger: registerPassenger,
              registerFunctionDriver: registerDriver),
          '/register/driver': (context) =>
              RegisterDriver(register: registerFunctionDriver, confirmPasswordController: confirmPassword,nameController: name,
                surnameController: surname,
                usernameController: username,passwordController: password,
                selectImage: selectImage,selectCarImage: selectCarImage,),
          '/register/passenger': (context) => RegisterPassenger(
                register: registerFunctionPassenger,
                nameController: name,
                surnameController: surname,
                usernameController: username,
                confirmPasswordController: confirmPassword,
                passwordController: password,
                selectImage: selectImage,
              ),
        },
        initialRoute: '/');
  }

  void registerScreen(BuildContext context) {
    Navigator.pushNamed(context, "/register");
  }

  void registerFunctionPassenger(BuildContext context) {
    if (confirmPassword.text == password.text) {
      if (image != null) {
        registerFunction();
        saveAdditionalData("passenger", username.text, name.text, surname.text);
        if (FirebaseAuth.instance.currentUser != null) {
          showToast("Ok",
              background: const Color.fromARGB(255, 0, 240, 8),
              textColor: const Color.fromARGB(255, 0, 55, 20));
          goToMap(context);
        }
      } else {
        showToast("Please select an image");
      }
    } else {
      showToast("Passwords do not match");
    }
  }

  void registerFunctionDriver(BuildContext context) {
    if (confirmPassword.text == password.text) {
      if (image != null) {
        if (carImage != null) {
          registerFunction();
          saveAdditionalData("driver", username.text, name.text, surname.text);
          if (FirebaseAuth.instance.currentUser != null) {
            showToast("Ok",
                background: const Color.fromARGB(255, 0, 240, 8),
                textColor: const Color.fromARGB(255, 0, 55, 20));
            goToMap(context);
          }
        } else {
          showToast("Please select a car image");
        }
      } else {
        showToast("Please select an image");
      }
    } else {
      showToast("Passwords do not match");
    }
  }

  void registerPassenger(BuildContext context) {
    Navigator.pushNamed(context, "/register/passenger");
  }

  void registerDriver(BuildContext context) {
    Navigator.pushNamed(context, "/register/driver");
  }

  void selectImage(BuildContext context) async {
    showToast("Selecting image...");
    image = await picker!.pickImage(source: ImageSource.gallery);
  }

  void selectCarImage(BuildContext context) async {
    showToast("Selecting image...");
    carImage = await picker!.pickImage(source: ImageSource.gallery);
  }

  void saveAdditionalData(
      String collection, String username, String name, String surname) async {
    showToast("Saving additional data...");
    String? url;
    if (image != null) {
      final ref = FirebaseStorage.instance
          .ref()
          .child(collection)
          .child('$username.jpg');
      await ref.putFile(File(image!.path));
      url = await ref.getDownloadURL();
      if (collection == "passenger") {
        await FirebaseFirestore.instance.collection(collection).add({
          "mail": username,
          "name": name,
          "surname": surname,
          "image": url,
          "inTrip": false
        });
      } else {
        if (carImage != null) {
          final ref = FirebaseStorage.instance
              .ref()
              .child(collection)
              .child('${username}_car.jpg');
          await ref.putFile(File(carImage!.path));
          String carUrl = await ref.getDownloadURL();
          await FirebaseFirestore.instance.collection(collection).add({
            "mail": username,
            "name": name,
            "surname": surname,
            "image": url,
            "carUrl": carUrl,
            "available": true
          });
        }
      }
    }
  }
}
