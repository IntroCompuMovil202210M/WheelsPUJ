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
            Expanded(
                child: Padding(
              padding: const EdgeInsets.all(15.0),
              child: TextField(
                obscureText: widget.hide,
                controller: _text,
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
