// ignore_for_file: use_build_context_synchronously

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:flutter_osm_plugin/flutter_osm_plugin.dart';
import 'package:wheelspuj/uses_map/map.dart';

logOut(BuildContext context) async {
  await FirebaseAuth.instance.signOut();
  Navigator.pushNamed(context, "/");
}

class Menu extends StatelessWidget {
  const Menu({Key? key, required this.mpc}) : super(key: key);
  final MapController mpc;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        debugShowCheckedModeBanner: false,
        home: Scaffold(
          body: const Map(),
          persistentFooterButtons: [
            FloatingActionButton(
                onPressed: () => logOut(context),
                child: const Icon(Icons.output_rounded)),
          ],
        ));
  }
}
