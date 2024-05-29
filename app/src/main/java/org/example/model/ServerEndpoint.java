package org.example.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ServerEndpoint {

  private static final String BASE_URL = "https://husksheets.fly.dev/api/v1/";
  private static final String USERNAME = "team2";
  private static final String PASSWORD = "Ltf3r008'fYrV405";

  private static String getBasicAuthHeader() {
    String auth = USERNAME + ":" + PASSWORD;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

//  public static void main(String[] args) throws Exception {
//    register("team2");
//    getPublishers();
  //createSheet("team2", "exampleSheet");
//    getSheets("team2");
  //deleteSheet("team2", "exampleSheet");
  //getSheets("team2");
//    updatePublished("team2", "exampleSheet", "NEW3");
//    getUpdatesForSubscription("team2", "exampleSheet", "1");
//    updateSubscription("team2", "exampleSheet", "NEW");
//    getUpdatesForPublished("team2", "exampleSheet", "3");
//  }


  /**
   * causes the server to create a publisher with the client name. No value is returned.
   *
   * @param publisher
   * @throws Exception
   */
  private static void register(String publisher) throws Exception {
    String url = BASE_URL + "register";
    HttpClient client = HttpClient.newBuilder().build();
    String json = String.format("{\"publisher\":\"%s\"}", publisher);
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .header("Authorization", getBasicAuthHeader())
            .header("Content-Type", "application/json")
            //.POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("Register Response: " + response.body());
  }

  /**
   * returns a list of argument objects with the publisher field set to all registered publishers.
   *
   * @throws Exception
   */
  private static void getPublishers() throws Exception {
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
   * takes an argument object with fields publisher and sheet set to the name of the client and
   * the name of a sheet to create. No value is returned.
   *
   * @param publisher
   * @param sheet
   * @throws Exception
   */
  private static void createSheet(String publisher, String sheet) throws Exception {
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
   * takes an argument object with field publisher set to the name of a publisher and
   * returns a list of argument objects with the publisher and sheet fields set to all
   * sheet names for the given publisher.
   *
   * @param publisher
   * @throws Exception
   */
  private static void getSheets(String publisher) throws Exception {
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
  }

  /**
   * takes an argument object with fields publisher and sheet set to the name of the client
   * and the name of a sheet to delete. No value is returned.
   *
   * @param publisher
   * @param sheet
   * @throws Exception
   */
  private static void deleteSheet(String publisher, String sheet) throws Exception {
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
   * takes an argument object with fields publisher, sheet and id set to the name of a publisher,
   * a sheet, and an id. It returns an argument object with the payload set to all updates that
   * occurred after id, and the id field set to the last id for those updates. The sheet is owned
   * by a publisher different from the client. An empty payload is returned if no updates occurred
   * after the given id.
   *
   * @param publisher
   * @param sheet
   * @param id
   * @throws Exception
   */
  private static void getUpdatesForSubscription(String publisher, String sheet, String id) throws Exception {
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
  }

  /**
   * takes an argument object with fields publisher, sheet and id set to the name of a publisher,
   * a sheet, and an id. It returns an argument object with the payload set to all the requests
   * for updates that occurred after id, and the id field set to the last id for those requests
   * for updates. The sheet is owned by the client. An empty payload is returned if no updates
   * occurred after the given id.
   *
   * @param publisher
   * @param sheet
   * @param id
   * @throws Exception
   */
  private static void getUpdatesForPublished(String publisher, String sheet, String id) throws Exception {
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

  /**
   * takes an argument object with fields publisher, sheet and payload set to the name of a
   * publisher, a sheet, and updates for that sheet. No value is returned. The sheet is owned by the client.
   *
   * @param publisher
   * @param sheet
   * @param payload
   * @throws Exception
   */
  private static void updatePublished(String publisher, String sheet, String payload) throws Exception {
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

  /**
   * takes an argument object with fields publisher, sheet and payload set to the name of a
   * publisher, a sheet, and requests for updates for that sheet. No value is returned. The
   * sheet is owned by a publisher different from the client.
   *
   * @param publisher
   * @param sheet
   * @param payload
   * @throws Exception
   */
  private static void updateSubscription(String publisher, String sheet, String payload) throws Exception {
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
