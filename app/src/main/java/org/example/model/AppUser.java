package org.example.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The AppUser class represents a user entity in the application.
 * It includes methods for user authentication and account creation via HTTP requests.
 */
public class AppUser implements IAppUser {

    @Id
    private String username;
    private String password;

    private List<ISpreadsheet> sheets;

    // Default constructor
    public AppUser() {
        this.sheets = new ArrayList<>();
    }

    // Getter for username
    public String getUsername() {
        return this.username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    public void addSheet(String sheetName) {
        this.sheets.add(new Spreadsheet(sheetName));
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

    /**
     * Authenticates a user with the provided username and password.
     * Sends a POST request to the /authenticate endpoint.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return a message indicating the result of the authentication
     */
//    public String authenticateUser(String username, String password) {
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//            String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI(uri + "/authenticate"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
//                    .build();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            if (response.statusCode() == 200) {
//                return "Login successful!";
//                // Uncomment the below code if you want to launch a new GUI on successful login
//                // SwingUtilities.invokeLater(new Runnable() {
//                // @Override
//                // public void run() {
//                // new MainGUI().setVisible(true);
//                // }
//                // });
//                // this.dispose(); // Close the login window
//            } else {
//                return "Failed to login: " + response.body();
//                // Uncomment the below code if you want to show a dialog on login failure
//                // JOptionPane.showMessageDialog(this, "Failed to login: " + response.body());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred: " + e.getMessage();
//            // Uncomment the below code if you want to show a dialog on error
//            // JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
//        }
//    }

    /**
     * Creates a new user account with the provided username and password.
     * Sends a POST request to the /register endpoint.
     *
     * @param username the username for the new account
     * @param password the password for the new account
     * @return a message indicating the result of the account creation
     */
//    public String createAccount(String username, String password) {
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//            String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI(uri + "/register"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
//                    .build();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                return "Account created successfully!";
//            } else {
//                return "Failed to create account: " + response.body();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred: " + e.getMessage();
//        }
//    }
//}
