package org.example.model;

import java.util.List;

/**
 * Represents the interface for application user management.
 */
public interface IAppUser {

  /**
   * Authenticates a user with the given username and password.
   *
   * @param username the username of the user.
   * @param password the password of the user.
   * @return a message indicating the result of the authentication process.
   */
  //String authenticateUser(String username, String password);

  /**
   * Creates a new account with the given username and password.
   *
   * @param username the desired username for the new account.
   * @param password the desired password for the new account.
   * @return a message indicating the result of the account creation process.
   */
  //String createAccount(String username, String password);

  /**
   * Gets the username of the user.
   *
   * @return the username of the user.
   */
  String getUsername();

  /**
   * Sets the username of the user.
   *
   * @param username the new username of the user.
   */
  void setUsername(String username);

  /**
   * Gets the password of the user.
   *
   * @return the password of the user.
   */
  String getPassword();

  /**
   * Sets the password of the user.
   *
   * @param password the new password of the user.
   */
  void setPassword(String password);

  /**
   * Checks if a sheet with the given name exists.
   *
   * @param name the name of the sheet to check.
   * @return true if the sheet exists, false otherwise.
   */
  boolean doesSheetExist(String name);

  /**
   * Adds a new sheet with the given name.
   *
   * @param sheetName the name of the sheet to add.
   */
  void addSheet(String sheetName);

  /**
   * Gets the list of spreadsheets associated with the user.
   *
   * @return a list of spreadsheets.
   */
  List<ISpreadsheet> getSheets();
}
