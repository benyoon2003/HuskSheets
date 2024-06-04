package org.example;

import org.example.controller.IUserController;
import org.example.controller.UserController;
import org.example.model.AppUser;
import org.example.model.Home;
import org.example.model.Spreadsheet;
import org.example.view.HomeView;
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
    new UserController(new LoginView());
  }
}
