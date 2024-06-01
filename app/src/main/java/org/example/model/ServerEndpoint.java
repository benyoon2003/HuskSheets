package org.example.model;


import org.example.controller.ConfigLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ServerEndpoint {


  // Base URL for the server endpoints
  private static final String BASE_URL = ConfigLoader.getProperty("base.url");
  // Username for authentication
  private static final String USERNAME = ConfigLoader.getProperty("username");
  // Password for authentication
  private static final String PASSWORD = ConfigLoader.getProperty("password");

  /**
   * Constructs the Basic Authentication header using the username and password.
   *
   * @return Basic Authentication header string
   */
  private static String getBasicAuthHeader() {
    String auth = USERNAME + ":" + PASSWORD;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Registers a publisher with the server.
   *
   * @param publisher Name of the publisher to register
   * @throws Exception if an error occurs during the HTTP request
   */
  private static void register(String publisher) throws Exception {
    String url = BASE_URL + "register";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\"}", publisher);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Register Response: " + response.body());
  }

  /**
   * Retrieves the list of publishers from the server.
   *
   * @throws Exception if an error occurs during the HTTP request
   */
  public static void getPublishers() throws Exception {
    String url = BASE_URL + "getPublishers";
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Get Publishers Response: " + response.body());
  }

  /**
   * Creates a new sheet for a specified publisher on the server.
   *
   * @param publisher Name of the publisher
   * @param sheet     Name of the sheet to create
   * @throws Exception if an error occurs during the HTTP request
   */
  public static void createSheet(String publisher, String sheet) throws Exception {
    String url = BASE_URL + "createSheet";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\"}", publisher, sheet);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Create Sheet Response: " + response.body());
  }

  /**
   * Retrieves the list of sheets for a specified publisher from the server.
   *
   * @param publisher Name of the publisher
   * @return Response body containing the list of sheets
   * @throws Exception if an error occurs during the HTTP request
   */
  public static String getSheets(String publisher) throws Exception {
    String url = BASE_URL + "getSheets";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\"}", publisher);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Get Sheets Response: " + response.body());
    return response.body();
  }

  /**
   * Deletes a specified sheet for a publisher from the server.
   *
   * @param publisher Name of the publisher
   * @param sheet     Name of the sheet to delete
   * @throws Exception if an error occurs during the HTTP request
   */
  public static void deleteSheet(String publisher, String sheet) throws Exception {
    String url = BASE_URL + "deleteSheet";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\"}", publisher, sheet);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Delete Sheet Response: " + response.body());
  }

  /**
   * Gets updates for a Subscriber of a specified sheet
   * @param publisher Name of publisher
   * @param sheet Name of sheet
   * @param id id of last version
   * @return Respones body containing payload of sheet updates
   * @throws Exception
   */
  public static String getUpdatesForSubscription(String publisher, String sheet, String id) throws Exception {
    String url = BASE_URL + "getUpdatesForSubscription";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"id\":\"%s\"}", publisher, sheet, id);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Get Updates For Subscription Response: " + response.body());
    return response.body();
  }

  public static void getUpdatesForPublished(String publisher, String sheet, String id) throws Exception {
    String url = BASE_URL + "getUpdatesForPublished";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"id\":\"%s\"}", publisher, sheet, id);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Get Updates For Published Response: " + response.body());
  }

  public static void updatePublished(String publisher, String sheet, String payload) throws Exception {
    String url = BASE_URL + "updatePublished";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", publisher, sheet, payload);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Update Published Response: " + response.body());
  }

  public static void updateSubscription(String publisher, String sheet, String payload) throws Exception {
    String url = BASE_URL + "updateSubscription";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", publisher, sheet, payload);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Update Subscription Response: " + response.body());
  }
}
