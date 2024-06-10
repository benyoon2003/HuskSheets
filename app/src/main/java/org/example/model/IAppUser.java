package org.example.model;

import java.util.List;

/**
 * Represents the interface for a User in the application.
 */
public interface IAppUser {

  /**
   * Gets the username of the user.
   *
   * @return the username of the user.
   * @author Ben
   */
  String getUsername();

  /**
   * Gets the password of the user.
   *
   * @return the password of the user.
   * @author Ben
   */
  String getPassword();

  /**
   * Adds a new sheet with the given name.
   *
   * @param sheetName the name of the sheet to add.
   * @author Tony
   */
  void addSheet(String sheetName);

  /**
   * Checks if a sheet with the given name exists.
   *
   * @param name the name of the sheet to check.
   * @return true if the sheet exists, false otherwise.
   * @author Ben
   */
  boolean doesSheetExist(String name);

  /**
   * Gets the list of spreadsheets associated with the user.
   *
   * @return a list of spreadsheets.
   * @author Ben
   */
  List<ISpreadsheet> getSheets();

  /**
   * Removes a given sheet name from the users list of sheets.
   *
   * @param sheetName the name of the sheet
   * @author Tony
   */
  void removeSheet(String sheetName);
}
