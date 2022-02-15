import 'package:flutter/material.dart';

class InputField extends StatefulWidget {
  final Icon icon;
  final String text;
  final bool hide;
  const InputField({
    Key? key,
    required this.icon,
    required this.text,
    this.hide = false,
  }) : super(key: key);
  @override
  _InputFieldState createState() => _InputFieldState();
}

class _InputFieldState extends State<InputField> {
  final _text = TextEditingController();
  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              widget.text,
              textAlign: TextAlign.center,
              style: const TextStyle(color: Color(0xff8fb9a8)),
            ),
            Expanded(
                child: Padding(
              padding: const EdgeInsets.all(15.0),
              child: TextField(
                obscureText: widget.hide,
                controller: _text,
                decoration: const InputDecoration(
                  border: UnderlineInputBorder()
                ),
              ),
            )),
            widget.icon
          ],
        ),
      ),
    );
  }
}
