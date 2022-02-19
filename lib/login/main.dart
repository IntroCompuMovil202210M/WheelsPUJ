import 'package:flutter/material.dart';

import 'package:wheelspuj/login/login.dart';

class Login extends StatelessWidget {
  final void Function(BuildContext context) logInButton;
  final void Function(BuildContext context) forgotPassword;
  final void Function(BuildContext context) register;
  final TextEditingController username;
  final TextEditingController password;
  const Login({
    Key? key,
    required this.logInButton,
    required this.username,
    required this.password,
    required this.forgotPassword,
    required this.register,
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
                  gradient: SweepGradient(colors: [
                    Color.fromARGB(255, 113, 66, 255),
                    Color.fromARGB(255, 130, 103, 255),
                    Color.fromARGB(255, 131, 255, 245),
                    Color.fromARGB(255, 113, 66, 209),
                    Color.fromARGB(255, 29, 179, 238),
                    Color.fromARGB(255, 255, 147, 183),
                    Color.fromARGB(255, 40, 183, 250),
                    Color.fromARGB(255, 132, 0, 255),
                    Color.fromARGB(255, 132, 241, 255),
                    Color.fromARGB(255, 113, 66, 255),
                    Color.fromARGB(255, 130, 103, 255),
                    Color.fromARGB(255, 131, 255, 245),
                    Color.fromARGB(255, 113, 66, 209),
                    Color.fromARGB(255, 29, 179, 238),
                    Color.fromARGB(255, 255, 147, 183),
                    Color.fromARGB(255, 40, 183, 250),
                    Color.fromARGB(255, 132, 0, 255),
                    Color.fromARGB(255, 132, 241, 255)
                  ], tileMode: TileMode.decal),
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
                            Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  const Expanded(
                                    child: Padding(
                                      padding: EdgeInsets.all(8.0),
                                      child:
                                          Text("Still don't have an account?"),
                                    ),
                                  ),
                                  Expanded(
                                      child: TextButton(
                                    style: ButtonStyle(
                                        backgroundColor:
                                            MaterialStateProperty.all(
                                                Colors.white)),
                                    onPressed: () {
                                      register(context);
                                    },
                                    child: const Text("Register"),
                                  )),
                                  Expanded(
                                    child: TextButton(
                                      style: ButtonStyle(
                                          backgroundColor:
                                              MaterialStateProperty.all(
                                                  Colors.white)),
                                      onPressed: () {
                                        forgotPassword(context);
                                      },
                                      child: const Text('Forgot password',
                                          style: TextStyle(
                                              color: Color.fromARGB(
                                                  255, 233, 37, 96),
                                              decorationColor: Color.fromARGB(
                                                  255, 233, 37, 96),
                                              decoration:
                                                  TextDecoration.underline)),
                                    ),
                                  ),
                                ]),
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
                                      logInButton(context);
                                    }),
                              ),
                            ),
                          ]),
                    ),
                  ),
                ),
              ),
            )));
  }
}
