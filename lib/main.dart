// ignore_for_file: use_build_context_synchronously
import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart'; // for Firebase
import 'package:firebase_auth/firebase_auth.dart'; // for FirebaseAuth
import 'package:wheelspuj/firebase_options.dart'; // Firebase Options
import 'package:wheelspuj/login/main.dart'; // Import Login page
import 'package:bot_toast/bot_toast.dart'; // Toast

TextEditingController username = TextEditingController();
TextEditingController password = TextEditingController();

void showToast(String msg, {Color? background, Color? textColor}) {
  if (background != null) {
    if (textColor != null) {
      BotToast.showText(
          text: msg, contentColor: textColor, backgroundColor: background);
    } else {
      BotToast.showText(
          text: msg,
          backgroundColor: background);
    }
  } else if (textColor != null) {
    BotToast.showText(
        text: msg, contentColor: textColor);
  } else {
    BotToast.showText(text: msg);
  }
}

void logInButton(BuildContext context) async {
  showToast("Trying log in...");
  try {
    await FirebaseAuth.instance.signInWithEmailAndPassword(
        email: username.text, password: password.text);
    if (FirebaseAuth.instance.currentUser != null) {
      showToast("Ok", background: const Color.fromARGB(255, 0, 240, 8), textColor: const Color.fromARGB(255, 0, 55, 20));
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

void forgotPassword(BuildContext context) {
  print("Forgot password");
}

void register(BuildContext context) async {
  print("Register");
  try {
    final credential =
        await FirebaseAuth.instance.createUserWithEmailAndPassword(
      email: username.text,
      password: password.text,
    );
  } on FirebaseAuthException catch (e) {
    if (e.code == 'weak-password') {
      showToast('The password provided is too weak');
    } else if (e.code == 'email-already-in-use') {
      showToast('The account already exists for the email ${username.text}');
    }
  } catch (e) {
    showToast(e.toString());
  }
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
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
              logInButton: logInButton,
              username: username,
              password: password,
              forgotPassword: forgotPassword,
              register: register)
        },
        initialRoute: '/');
  }
}
