import 'package:flutter/material.dart';

class InputField extends StatefulWidget {
  final Icon icon;
  final String text;
  final bool hide;
  const InputField(
      {Key? key,
      required this.icon,
      required this.text,
      this.hide = false,})
      : super(key: key);
  @override
  _InputFieldState createState() => _InputFieldState();
}

class _InputFieldState extends State<InputField> {
  final _text = TextEditingController();
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Expanded(child: widget.icon),
          Expanded(child: Text(widget.text)),
          Expanded(
            child: TextField(
              obscureText: widget.hide,
              controller: _text,
              decoration: const InputDecoration(
                border: InputBorder.none,
                fillColor: Colors.white,
                filled: true,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

/*

*/