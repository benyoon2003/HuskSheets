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

  boolean doesSheetExist(String name);

  void addSheet(String sheetName);
  List<ISpreadsheet> getSheets();
}
