// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';

class InputField extends StatefulWidget {
  final Icon icon;
  final String text;
  final TextEditingController textController;
  final bool hide;
  const InputField({
    Key? key,
    required this.icon,
    required this.text,
    required this.textController,
    this.hide = false,
  }) : super(key: key);
  @override
  _InputFieldState createState() => _InputFieldState();
}

class _InputFieldState extends State<InputField> {
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.all(5.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(
                child: Padding(
              padding: const EdgeInsets.all(2.0),
              child: TextField(
                obscureText: widget.hide,
                controller: widget.textController,
                decoration: InputDecoration(
                    suffixIcon: widget.icon,
                    hintText: widget.text,
                    hintStyle: const TextStyle(
                        fontSize: 18, fontWeight: FontWeight.w300),
                    border: const UnderlineInputBorder()),
              ),
            ))
          ],
        ),
      ),
    );
  }
}
