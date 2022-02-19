import 'package:flutter/material.dart';
import 'package:flutter_osm_plugin/flutter_osm_plugin.dart';
import 'package:wheelspuj/uses_map/map.dart';

class Menu extends StatelessWidget {
  const Menu({Key? key, required this.mpc}) : super(key: key);
  final MapController mpc;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        debugShowCheckedModeBanner: false,
        home: Scaffold(
          body: const Map(),
          bottomNavigationBar: BottomNavigationBar(
            backgroundColor: Colors.blueAccent,
            items: const <BottomNavigationBarItem>[
              BottomNavigationBarItem(label: "Hola", icon: Icon(Icons.abc)),
              BottomNavigationBarItem(label: "Hola2", icon: Icon(Icons.ac_unit))
            ],
          ),
        ));
  }
}
