import 'package:flutter/material.dart';
import 'package:wheelspuj/login/input_field.dart';

class Register extends StatelessWidget {
  final void Function(BuildContext context) registerFunctionPassenger;
  final void Function(BuildContext context) registerFunctionDriver;
  const Register(
      {Key? key,
      required this.registerFunctionPassenger,
      required this.registerFunctionDriver})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        alignment: Alignment.center,
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
        ], tileMode: TileMode.decal)),
        child: SafeArea(
          child: Container(
            decoration: const BoxDecoration(
                color: Color.fromARGB(255, 255, 255, 255),
                borderRadius: BorderRadius.all(Radius.circular(10.0))),
            child: Padding(
              padding: const EdgeInsets.all(15.0),
              child: Wrap(
                direction: Axis.vertical,
                alignment: WrapAlignment.center,
                crossAxisAlignment: WrapCrossAlignment.center,
                children: [
                  ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        primary:
                            const Color.fromARGB(255, 4, 47, 200), // background
                        onPrimary: const Color.fromARGB(
                            255, 10, 158, 123), // foreground
                      ),
                      onPressed: () => registerFunctionPassenger(context),
                      child: const Text("Passenger")),
                  ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        primary:
                            const Color.fromARGB(255, 4, 47, 200), // background
                        onPrimary: const Color.fromARGB(
                            255, 10, 158, 123), // foreground
                      ),
                      onPressed: () => registerFunctionDriver(context),
                      child: const Text("Driver")),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class RegisterDriver extends StatelessWidget {
  final Function(BuildContext context) register;
  final Function(BuildContext context) selectImage;
  final Function(BuildContext context) selectCarImage;
  final TextEditingController nameController;
  final TextEditingController surnameController;
  final TextEditingController usernameController;
  final TextEditingController passwordController;
  final TextEditingController confirmPasswordController;
  final TextEditingController licenseController;
  const RegisterDriver(
      {Key? key,
      required this.register,
      required this.selectImage,
      required this.selectCarImage,
      required this.nameController,
      required this.surnameController,
      required this.usernameController,
      required this.passwordController,
      required this.confirmPasswordController, required this.licenseController})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        resizeToAvoidBottomInset: false,
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
            ], tileMode: TileMode.decal)),
            child: SafeArea(
              child: Padding(
                padding: const EdgeInsets.all(50.0),
                child: Container(
                  decoration: const BoxDecoration(
                    color: Color.fromARGB(255, 255, 255, 255),
                    borderRadius: BorderRadius.all(Radius.circular(10.0)),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Image.asset(
                            'icons/main.png',
                            height: 150,
                            width: 150,
                          ),
                          Expanded(
                            child: InputField(
                                textController: usernameController,
                                icon: const Icon(
                                  Icons.account_circle_rounded,
                                  color: Color.fromARGB(255, 116, 136, 30),
                                ),
                                text: "Username..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: nameController,
                                icon: const Icon(null),
                                text: "Name..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: surnameController,
                                icon: const Icon(null),
                                text: "Surname..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: passwordController,
                                icon: const Icon(Icons.password),hide:true,
                                text: "Password..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: confirmPasswordController,
                                icon: const Icon(Icons.password),hide:true,
                                text: "Confirm password..."),
                                
                          ),Expanded(
                            child: InputField(
                                textController: licenseController,
                                icon: const Icon(Icons.car_rental),
                                text: "License plate..."),
                          ),
                          Row(
                            children: [
                              Expanded(
                                  child: ElevatedButton(
                                      style: ElevatedButton.styleFrom(
                                        primary: const Color.fromARGB(
                                            255, 74, 109, 233), // background
                                        onPrimary: const Color.fromARGB(
                                            255, 6, 1, 60), // foreground
                                      ),
                                      onPressed: () => selectImage(context),
                                      child: const Text("Select your Image..."))),
                                      Expanded(
                                  child: ElevatedButton(
                                      style: ElevatedButton.styleFrom(
                                        primary: const Color.fromARGB(
                                            255, 74, 109, 233), // background
                                        onPrimary: const Color.fromARGB(
                                            255, 6, 1, 60), // foreground
                                      ),
                                      onPressed: () => selectCarImage(context),
                                      child: const Text("Select your car Image..."))),
                            ],
                          ),
                          ElevatedButton(
                              style: ElevatedButton.styleFrom(
                                primary: const Color.fromARGB(
                                    255, 4, 47, 200), // background
                                onPrimary: const Color.fromARGB(
                                    255, 255, 255, 255), // foreground
                              ),
                              onPressed: () => register(context),
                              child: const Text("Register"))
                        ]),
                  ),
                ),
              ),
            ),
          ),
        ));
  }
}

class RegisterPassenger extends StatelessWidget {
  final Function(BuildContext context) register;
  final Function(BuildContext context) selectImage;
  final TextEditingController nameController;
  final TextEditingController surnameController;
  final TextEditingController usernameController;
  final TextEditingController passwordController;
  final TextEditingController confirmPasswordController;
  const RegisterPassenger(
      {Key? key,
      required this.register,
      required this.nameController,
      required this.surnameController,
      required this.usernameController,
      required this.passwordController,
      required this.confirmPasswordController,
      required this.selectImage})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        resizeToAvoidBottomInset: false,
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
            ], tileMode: TileMode.decal)),
            child: SafeArea(
              child: Padding(
                padding: const EdgeInsets.all(50.0),
                child: Container(
                  decoration: const BoxDecoration(
                    color: Color.fromARGB(255, 255, 255, 255),
                    borderRadius: BorderRadius.all(Radius.circular(10.0)),
                  ),
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Image.asset(
                            'icons/main.png',
                            height: 150,
                            width: 150,
                          ),
                          Expanded(
                            child: InputField(
                                textController: usernameController,
                                icon: const Icon(
                                  Icons.account_circle_rounded,
                                  color: Color.fromARGB(255, 116, 136, 30),
                                ),
                                text: "Username..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: nameController,
                                icon: const Icon(null),
                                text: "Name..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: surnameController,
                                icon: const Icon(null),
                                text: "Surname..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: passwordController,
                                icon: const Icon(Icons.password),hide:true,
                                text: "Password..."),
                          ),
                          Expanded(
                            child: InputField(
                                textController: confirmPasswordController,
                                icon: const Icon(Icons.password),hide:true,
                                text: "Confirm password..."),
                          ),
                          Expanded(
                              child: ElevatedButton(
                                  style: ElevatedButton.styleFrom(
                                    primary: const Color.fromARGB(
                                        255, 74, 109, 233), // background
                                    onPrimary: const Color.fromARGB(
                                        255, 6, 1, 60), // foreground
                                  ),
                                  onPressed: () => selectImage(context),
                                  child: const Text("Select your Image..."))),
                          ElevatedButton(
                              style: ElevatedButton.styleFrom(
                                primary: const Color.fromARGB(
                                    255, 4, 47, 200), // background
                                onPrimary: const Color.fromARGB(
                                    255, 255, 255, 255), // foreground
                              ),
                              onPressed: () => register(context),
                              child: const Text("Register"))
                        ]),
                  ),
                ),
              ),
            ),
          ),
        ));
  }
}
