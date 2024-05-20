package org.example.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users") // Ensure the table name is not a reserved keyword
public class AppUser implements IAppUser {

    @Id
    private String username;
    private String password;

    private String uri = "http://localhost:8080/api";// "https://husksheets.fly.dev/api/v1";

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AppUser() {

    }

    public String authenticateUser(String username, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri + "/authenticate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return "Login successful!";
                // SwingUtilities.invokeLater(new Runnable() {
                // @Override
                // public void run() {
                // new MainGUI().setVisible(true);
                // }
                // });
                // this.dispose(); // Close the login window
            } else {
                return "Failed to login: " + response.body();
                // JOptionPane.showMessageDialog(this, "Failed to login: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
            // JOptionPane.showMessageDialog(this, "Error occurred: " + e.getMessage());
        }
    }

    public String createAccount(String username, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return "Account created successfully!";
            } else {
                return "Failed to create account: " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

}
