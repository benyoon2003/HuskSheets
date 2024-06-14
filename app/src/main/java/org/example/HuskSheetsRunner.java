package org.example;

import org.example.controller.UserController;
import org.example.view.LoginView;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The HuskSheetsRunner class serves as the entry point for running the Husk Sheets application.
 * It initializes the user controller with the login view to start the application.
 */

public class HuskSheetsRunner {

    /**
     * The main method is the entry point of the application.
     * It creates a new instance of the UserController with the LoginView.
     *
     * @param args command line arguments passed to the application.
     */
  public static void main(String[] args) {
      if (args.length == 5) {
          // Map to store the command-line arguments and their values
          Map<String, String> arguments = new HashMap<>();

          // Loop through the arguments and parse them
          for (String arg : args) {
              if (arg.startsWith("--")) {
                  String[] splitArg = arg.split("=", 2);
                  if (splitArg.length == 2) {
                      String key = splitArg[0].substring(2); // Remove the leading --
                      String value = splitArg[1].replaceAll("\"", ""); // Remove quotes
                      arguments.put(key, value);
                  }
              }
          }

          // Access the parsed arguments
          String url = arguments.get("url");
          String name = arguments.get("name");
          String password = arguments.get("password");
          String publisher = arguments.get("publisher");
          String sheet = arguments.get("sheet");
          new UserController(url, name, password, publisher, sheet);
      }
      else if (args.length == 1) {
          String url = args[0];
          new UserController(new LoginView(), url);
      }
      else {
          new UserController(new LoginView());
      }
  }
}
