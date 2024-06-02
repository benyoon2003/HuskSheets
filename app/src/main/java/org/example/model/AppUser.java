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

    private String username;
    private String password;

    // Base URI for the API endpoints
    private String uri = "http://localhost:8080/api"; // "https://husksheets.fly.dev/api/v1";

    private List<Spreadsheet> published;
    // Default constructor
    public AppUser() {
        this.published = new ArrayList<>();
    }

    // Getter for username
    public String getUsername() {
        return "team2";
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

    public void addPublished(Spreadsheet sheet){
        this.published.add(sheet);
    }

    public void removePublished(Spreadsheet sheet){
        this.published.remove(sheet);
    }

    public List<Spreadsheet> getPublished() {
        return published;
    }

}
