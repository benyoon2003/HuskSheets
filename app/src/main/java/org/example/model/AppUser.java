package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The AppUser class represents a user entity in the application.
 * It includes methods for user authentication and account creation via HTTP requests.
 */
public class AppUser implements IAppUser {
    private String username; // Stores the username of the user
    private String password; // Stores the password of the user

    private List<ISpreadsheet> sheets; // List to store the user's sheets

    /**
     * Default constructor for AppUser.
     *
     * @param username a username
     * @param password a password
     */
    public AppUser(String username, String password) {
        this.sheets = new ArrayList<>(); // Initialize the list of sheets
        this.username = username; // Set the username
        this.password = password; // Set the password
    }

    @Override
    public String getUsername() {
        return this.username; // Return the username
    }

    @Override
    public String getPassword() {
        return password; // Return the password
    }

    @Override
    public void addSheet(String sheetName) {
        this.sheets.add(new Spreadsheet(sheetName)); // Add a new sheet to the list
    }

    @Override
    public void removeSheet(String sheetName) {
        for (ISpreadsheet sheet : this.sheets) { // Iterate over the list of sheets
            if (sheet.getName().equals(sheetName)) { // Check if the sheet name matches
                this.sheets.remove(sheet); // Remove the sheet from the list
                return; // Exit the method once the sheet is removed
            }
        }
    }

    @Override
    public boolean doesSheetExist(String name) {
        for (ISpreadsheet sheet : this.sheets) { // Iterate over the list of sheets
            if (sheet.getName().equals(name)) { // Check if the sheet name matches
                return true; // Return true if the sheet exists
            }
        }
        return false; // Return false if the sheet does not exist
    }

    @Override
    public List<ISpreadsheet> getSheets() {
        return this.sheets; // Return the list of sheets
    }
}
