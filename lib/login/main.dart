import 'package:flutter/material.dart';

import 'package:wheelspuj/login/login.dart';

class Login extends StatelessWidget {
  final void Function() logInButton;
  final TextEditingController username = TextEditingController();
  final TextEditingController password = TextEditingController();
  Login({
    Key? key,
    required this.logInButton,
  }) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        color: Colors.white,
        debugShowCheckedModeBanner: false,
        title: 'Login page',
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        home: Scaffold(
          backgroundColor: Colors.white,
          body: Container(
              alignment: Alignment.center,
              child: Container(
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                      begin: Alignment.topRight,
                      end: Alignment.bottomLeft,
                      colors: [
                        Color.fromARGB(255, 246, 194, 255),
                        Color.fromARGB(255, 255, 241, 241),
                        Color.fromARGB(255, 128, 196, 190),
                        Color.fromARGB(255, 215, 242, 255),
                        Color.fromARGB(255, 255, 255, 255),
                        Color.fromARGB(255, 255, 147, 183),
                        Color.fromARGB(255, 239, 250, 255),
                        Color.fromARGB(137, 255, 255, 255),
                        Color.fromARGB(255, 188, 247, 255)
                      ]),
                  borderRadius: BorderRadius.all(Radius.circular(10.0)),
                ),
                child: SafeArea(
                  child: Padding(
                    padding: const EdgeInsets.all(50.0),
                    child: Container(
                      decoration: const BoxDecoration(
                        color: Color.fromARGB(255, 255, 255, 255),
                        borderRadius: BorderRadius.all(Radius.circular(30.0)),
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Expanded(
                            child: Container(
                              height: 50,
                            ),
                          ),
                          Image.asset(
                            'icons/main.png',
                            height: 150,
                            width: 150,
                          ),
                          Expanded(
                            child: Container(
                              height: 100,
                            ),
                          ),
                          Expanded(
                            child: InputField(
                                textController: username,
                                icon: const Icon(
                                  Icons.verified_user_rounded,
                                  color: Color.fromARGB(255, 0, 168, 6),
                                ),
                                text: "Username"),
                          ),
                          Expanded(
                            child: Container(
                              height: 50,
                            ),
                          ),
                          Expanded(
                            child: InputField(
                              textController: password,
                              icon: const Icon(
                                Icons.security_rounded,
                                color: Colors.red,
                              ),
                              text: "Password",
                              hide: true,
                            ),
                          ),
                          Expanded(
                            child: Container(
                              height: 80,
                            ),
                          ),
                          Padding(
                            padding: const EdgeInsets.all(15.0),
                            child: Expanded(
                              child: ElevatedButton(
                                  style: ButtonStyle(
                                      backgroundColor:
                                          MaterialStateProperty.all(
                                              Colors.blue)),
                                  child: const Text('Log In'),
                                  onPressed: () {
                                    logInButton();
                                  }),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              )),
        ));
  }
}
