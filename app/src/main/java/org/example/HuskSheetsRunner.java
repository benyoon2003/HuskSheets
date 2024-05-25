package org.example;

import org.example.controller.UserController;
import org.example.model.AppUser;
import org.example.model.Spreadsheet;
import org.example.view.HomeView;
import org.example.view.LoginView;

public class HuskSheetsRunner {
  public static void main(String[] args) {
    new UserController(
            new LoginView(), new HomeView(), new AppUser(),new Spreadsheet("test"));
  }
}
