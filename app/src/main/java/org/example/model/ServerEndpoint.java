package org.example.model;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ServerEndpoint {


  // Base URL for the server endpoints
  private static String BASE_URL = "https://husksheet-cb47d5864e1b.herokuapp.com/api/v1/";
  private static IAppUser user;


  public ServerEndpoint() {
  }
  /**
   * Constructs the Basic Authentication header using the username and password.
   *
   * @return Basic Authentication header string
   */
  private String getBasicAuthHeader() {
    String username = this.user.getUsername();
    String password = this.user.getPassword();
    String auth = username + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Registers a publisher with the server.
   *

   * @throws Exception if an error occurs during the HTTP request
   */

  public Result register(IAppUser user) throws Exception {
    this.user = user;
    // Encode the publisher name to be URL-safe
    String encodedPublisher = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
    String url = BASE_URL + "register?publisher=" + encodedPublisher;
    HttpResponse<String> response = sendGetRequest(url);
    System.out.println("Register request: " + response.body());
    return new Result(response.body());
  }

    /**
     * Logs in a publisher with the server.
     *
     * @param user the user to log in.
     * @return the result of the login.
     * @throws Exception if an error occurs during the HTTP request.
     */
  public Result login(IAppUser user) throws Exception {
    this.user = user;
    // Encode the publisher name to be URL-safe
    String encodedPublisher = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
    String url = BASE_URL + "login?publisher=" + encodedPublisher;
    HttpResponse<String> response = sendGetRequest(url);
    System.out.println("Login request: " + response.body());
    return new Result(response.body());
  }

  /**
   * Retrieves the list of publishers from the server.
   *
   * @throws Exception if an error occurs during the HTTP request
   */
  public Result getPublishers() throws Exception {
    String url = BASE_URL + "getPublishers";
    HttpResponse<String> response = sendGetRequest(url);
    System.out.println("Get Publishers Response: " + response.body());
    return new Result(response.body());
  }

  /**
   * Creates a new sheet for a specified publisher on the server.
   *
   * @param sheet     Name of the sheet to create
   * @throws Exception if an error occurs during the HTTP request
   */
  public Result createSheet(String sheet) throws Exception {
    String url = BASE_URL + "createSheet"; // Ensure the endpoint is correct
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\"}", user.getUsername(), sheet);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Create Sheet Response: " + response.body());
    return new Result(response.body());
  }


  /**
   * Retrieves the list of sheets for a specified publisher from the server.
   *
   * @return Response body containing the list of sheets
   * @throws Exception if an error occurs during the HTTP request
   */
  public Result getSheets(String publisher) throws Exception {
    String url = BASE_URL + "getSheets";
    String json = String.format("{\"publisher\":\"%s\"}", publisher);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Get Sheets Response: " + response.body());
    return new Result(response.body());
  }

  /**
   * Deletes a specified sheet for a publisher from the server.
   *
   * @param publisher Name of the publisher
   * @param sheet     Name of the sheet to delete
   * @throws Exception if an error occurs during the HTTP request
   */
  public Result deleteSheet(String publisher, String sheet) throws Exception {
    String url = BASE_URL + "deleteSheet";
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\"}", publisher, sheet);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Delete Sheet Response: " + response.body());
    return new Result(response.body());
  }

  /**
   * Gets updates for a Subscriber of a specified sheet
   * @param publisher Name of publisher
   * @param sheet Name of sheet
   * @param id id of last version
   * @return Respones body containing payload of sheet updates
   * @throws Exception
   */
  public Result getUpdatesForSubscription(String publisher, String sheet, String id) throws Exception {
    String url = BASE_URL + "getUpdatesForSubscription";
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"id\":\"%s\"}", publisher, sheet, id);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Get Updates For Subscription Response: " + response.body());
    return new Result(response.body());
  }

    /**
     * Retrieves updates for a published sheet.
     *
     * @param publisher the name of the publisher.
     * @param sheet     the name of the sheet.
     * @param id        the id of the last version.
     * @throws Exception if an error occurs during the HTTP request.
     */
  public Result getUpdatesForPublished(String publisher, String sheet, String id) throws Exception {
    String url = BASE_URL + "getUpdatesForPublished";
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"id\":\"%s\"}", publisher, sheet, id);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Get Updates For Published Response: " + response.body());
    return new Result(response.body());
  }

    /**
     * Updates a published sheet with new payload data.
     *
     * @param publisher the name of the publisher.
     * @param sheet     the name of the sheet.
     * @param payload   the new payload data.
     * @return the result of the update operation.
     * @throws Exception if an error occurs during the HTTP request.
     */
  public Result updatePublished(String publisher, String sheet, String payload) throws Exception {
    String url = BASE_URL + "updatePublished";
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", publisher, sheet, payload);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Update Published Response: " + response.body());
    return new Result(response.body());

}

    /**
     * Updates a subscription sheet with new payload data.
     *
     * @param publisher the name of the publisher.
     * @param sheet     the name of the sheet.
     * @param payload   the new payload data.
     * @throws Exception if an error occurs during the HTTP request.
     */
  public Result updateSubscription(String publisher, String sheet, String payload) throws Exception {
    String url = BASE_URL + "updateSubscription";
    String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", publisher, sheet, payload);
    HttpResponse<String> response = sendPostRequest(url, json);
    System.out.println("Update Subscription Response: " + response.body());
    return new Result(response.body());
  }


  /**
   * Send POST request to specified url
   * @param url destination of request
   * @param json content of request
   * @return response object
   * @throws Exception
   */
  public HttpResponse sendPostRequest(String url, String json) throws Exception {
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

      return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  /**
   * Send GET request to specified url
   * @param url destination of request
   * @return response object
   * @throws Exception
   */

  public HttpResponse sendGetRequest(String url) throws Exception {
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .GET()
            .build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }
}
