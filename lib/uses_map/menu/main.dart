// ignore_for_file: use_build_context_synchronously

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';

logOut(BuildContext context) async {
  await FirebaseAuth.instance.signOut();
  Navigator.pushNamed(context, "/");
}

class Menu extends StatelessWidget {
  const Menu({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        debugShowCheckedModeBanner: false,
        home: Scaffold(
          body: Container(),
          persistentFooterButtons: [
            FloatingActionButton(
                onPressed: () => logOut(context),
                child: const Icon(Icons.output_rounded)),
          ],
        ));
  }
}