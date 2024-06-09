package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The AppUser class represents a user entity in the application.
 * It includes methods for user authentication and account creation via HTTP requests.
 */
public class AppUser implements IAppUser {
    private String username;
    private String password;

    private List<ISpreadsheet> sheets;

    /**
     * Default constructor for AppUser.
     * @param username a username
     * @param password a password
     */
    public AppUser(String username, String password) {
        this.sheets = new ArrayList<>();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }

    public void addSheet(String sheetName) {
        this.sheets.add(new Spreadsheet(sheetName));
    }

    public void removeSheet(String sheetName) {
        for (ISpreadsheet sheet : this.sheets) {
            if (sheet.getName().equals(sheetName)) {
                this.sheets.remove(sheet);
                return;
            }
        }
    }

    public boolean doesSheetExist(String name) {
        for (ISpreadsheet sheet : this.sheets) {
            if (sheet.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public List<ISpreadsheet> getSheets() {
        return this.sheets;
    }

}
