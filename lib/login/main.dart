import 'package:flutter/material.dart';
import 'package:wheelspuj/login/login.dart';
class Login extends StatelessWidget {
  final void Function() logInButton;
  const Login({Key? key, required this.logInButton}) : super(key: key);
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
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                      begin: Alignment.topRight,
                      end: Alignment.bottomLeft,
                      colors: [
                        Colors.purple.shade50,
                        Colors.white30,
                        Colors.teal.shade100,
                        Colors.lightBlue.shade50,
                        Colors.white70,
                        Colors.pink.shade50,
                        Colors.blueGrey.shade100,
                        Colors.white54,
                        Colors.cyan.shade100
                      ]),
                  borderRadius: const BorderRadius.all(Radius.circular(10.0)),
                ),
                child: SafeArea(
                  child: Padding(
                    padding: const EdgeInsets.all(100.0),
                    child: Container(
                      decoration: const BoxDecoration(
                        color: Colors.white,
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
                          const Expanded(
                            child: InputField(
                                icon: Icon(Icons.verified_user_rounded,color: Colors.green,),
                                text: "Username"),
                          ),
                          Expanded(
                            child: Container(
                              height: 50,
                            ),
                          ),
                          const Expanded(
                            child: InputField(
                              icon: Icon(Icons.security_rounded,color: Colors.red,),
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
                              child: ElevatedButton(style: ButtonStyle(backgroundColor: MaterialStateProperty.all(Colors.blue)),
                                child: const Text('Log In'),
                                onPressed: logInButton,
                              ),
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                ),
              )),
        ));
  }
}
