package org.example;

import org.example.controller.UserController;
import org.example.view.LoginView;

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

      for(String arg : args) {
          System.out.println(arg);
      }

      new UserController(new LoginView());
  }
}
