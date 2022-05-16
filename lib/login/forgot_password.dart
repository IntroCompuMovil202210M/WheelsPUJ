import 'package:flutter/material.dart';
import 'package:wheelspuj/login/input_field.dart';

class ForgotPassword extends StatelessWidget {
  final void Function(BuildContext context) forgotPassword;
  final TextEditingController usernameController;

  const ForgotPassword(
      {Key? key,
      required this.forgotPassword,
      required this.usernameController})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.lightBlueAccent,
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const SizedBox(height: 50),
              const Text(
                'Email Your Email',
                style: TextStyle(fontSize: 30, color: Colors.white),
              ),
              Expanded(
                child: InputField(
                    icon: const Icon(Icons.email),
                    text: "Email...",
                    textController: usernameController),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                child: const Text('Send Email'),
                onPressed: () => forgotPassword(context),
              ),
              ElevatedButton(
                onPressed: () => Navigator.pushNamed(context, "/"),
                child: const Text('Sign In'),
              )
            ],
          ),
        ),
      ),
    );
  }
}
