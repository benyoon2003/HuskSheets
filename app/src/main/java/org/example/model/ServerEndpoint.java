package org.example.model;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;

public class ServerEndpoint {
    // Base URL for the server endpoints
    private String url = "https://localhost:8080/api/v1/"; //"https://husksheet-cb47d5864e1b.herokuapp.com/api/v1/";
    private static IAppUser user; // Static variable to hold the current user

    /**
     * Default constructor
     */
    public ServerEndpoint() {
        // Default constructor
    }

    /**
     * Constructor that allows for different URL
     *
     * @param url
     */
    public ServerEndpoint(String url) {
        this.url = url; // Set the URL to the provided value
    }

    /**
     * Constructs the Basic Authentication header using the username and password.
     *
     * @return Basic Authentication header string
     * @author Ben
     */
    private String getBasicAuthHeader() {
        String username = user.getUsername(); // Get the username from the user
        String password = user.getPassword(); // Get the password from the user
        String auth = username + ":" + password; // Combine username and password
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)); // Encode the combined string in Base64 and return as Basic Auth header
    }

    /**
     * Registers a publisher with the server.
     *
     * @throws Exception if an error occurs during the HTTP request
     * @author Vinay
     */

    public Result register(IAppUser user) throws Exception {
        ServerEndpoint.user = user; // Set the static user variable to the provided user
        String url = this.url + "register"; // Append "register" to the base URL
        HttpResponse<String> response = sendGetRequest(url); // Send GET request to register endpoint
        System.out.println("Register request: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Logs in a publisher with the server.
     *
     * @param user the user to log in.
     * @return the result of the login.
     * @throws Exception if an error occurs during the HTTP request
     * @author Ben
     */
    public Result login(IAppUser user) throws Exception {
        ServerEndpoint.user = user; // Set the static user variable to the provided user
        String url = this.url + "login"; // Append "login" to the base URL
        HttpResponse<String> response = sendGetRequest(url); // Send GET request to login endpoint
        System.out.println("Login request: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Retrieves the list of publishers from the server.
     *
     * @throws Exception if an error occurs during the HTTP request
     * @author Tony
     */
    public Result getPublishers() throws Exception {
        String url = this.url + "getPublishers"; // Append "getPublishers" to the base URL
        HttpResponse<String> response = sendGetRequest(url); // Send GET request to getPublishers endpoint
        System.out.println("Get Publishers Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Creates a new sheet for a specified publisher on the server.
     *
     * @param sheet Name of the sheet to create
     * @throws Exception if an error occurs during the HTTP request
     * @author Tony
     */
    public Result createSheet(String sheet) throws Exception {
        String url = this.url + "createSheet"; // Append "createSheet" to the base URL
        // Create JSON payload with publisher and sheet names
        String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\"}", user.getUsername(), sheet);
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to createSheet endpoint
        System.out.println("Create Sheet Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Retrieves the list of sheets for a specified publisher from the server.
     *
     * @return Response body containing the list of sheets
     * @throws Exception if an error occurs during the HTTP request
     * @author Vinay
     */
    public Result getSheets(String publisher) throws Exception {
        String url = this.url + "getSheets"; // Append "getSheets" to the base URL
        // Create JSON payload with publisher name
        String json = String.format("{\"publisher\":\"%s\"}", publisher);
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to getSheets endpoint
        System.out.println("Get Sheets Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Deletes a specified sheet for a publisher from the server.
     *
     * @param publisher Name of the publisher
     * @param sheet     Name of the sheet to delete
     * @throws Exception if an error occurs during the HTTP request
     * @author Tony
     */
    public Result deleteSheet(String publisher, String sheet) throws Exception {
        String url = this.url + "deleteSheet"; // Append "deleteSheet" to the base URL
        // Create JSON payload with publisher and sheet names
        String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\"}", publisher, sheet); 
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to deleteSheet endpoint
        System.out.println("Delete Sheet Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Gets updates for a Subscriber of a specified sheet
     *
     * @param publisher Name of publisher
     * @param sheet     Name of sheet
     * @param id        id of last version
     * @return Respones body containing payload of sheet updates
     * @throws Exception
     * @author Tony
     */
    public Result getUpdatesForSubscription(String publisher, String sheet, String id) throws Exception {
        String url = this.url + "getUpdatesForSubscription"; // Append "getUpdatesForSubscription" to the base URL
        // Create JSON payload with publisher, sheet, and ID
        String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"id\":\"%s\"}", publisher, sheet, id);
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to getUpdatesForSubscription endpoint
        System.out.println("Get Updates For Subscription Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Retrieves updates for a published sheet.
     *
     * @param publisher the name of the publisher.
     * @param sheet     the name of the sheet.
     * @param id        the id of the last version.
     * @throws Exception if an error occurs during the HTTP request.
     * @author Tony
     */
    public Result getUpdatesForPublished(String publisher, String sheet, String id) throws Exception {
        String url = this.url + "getUpdatesForPublished"; // Append "getUpdatesForPublished" to the base URL
        // Create JSON payload with publisher, sheet, and ID
        String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"id\":\"%s\"}", publisher, sheet, id);
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to getUpdatedForPublished endpoint
        System.out.println("Get Updates For Published Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }

    /**
     * Updates a published sheet with new payload data.
     *
     * @param publisher the name of the publisher.
     * @param sheet     the name of the sheet.
     * @param payload   the new payload data.
     * @return the result of the update operation.
     * @throws Exception if an error occurs during the HTTP request.
     * @author Vinay
     */
    public Result updatePublished(String publisher, String sheet, String payload) throws Exception {
        String url = this.url + "updatePublished"; // Append "updatePublished" to the base URL
        // Create JSON payload with publisher, sheet, and payload
        String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", publisher, sheet, payload);
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to updatePublished endpoint
        System.out.println("Update Published Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body

    }

    /**
     * Updates a subscription sheet with new payload data.
     *
     * @param publisher the name of the publisher.
     * @param sheet     the name of the sheet.
     * @param payload   the new payload data.
     * @throws Exception if an error occurs during the HTTP request.
     * @author Tony
     */
    public Result updateSubscription(String publisher, String sheet, String payload) throws Exception {
        String url = this.url + "updateSubscription"; // Append "updateSubscription" to the base URL
        // Create JSON payload with publisher, sheet, and payload
        String json = String.format("{\"publisher\":\"%s\", \"sheet\":\"%s\", \"payload\":\"%s\"}", publisher, sheet, payload);
        HttpResponse<String> response = sendPostRequest(url, json); // Send POST request to updateSubscription endpoint
        System.out.println("Update Subscription Response: " + response.body()); // Print the response body
        return new Result(response.body()); // Create and return a new Result from the response body
    }


    /**
     * Send POST request to specified url
     *
     * @param url  destination of request
     * @param json content of request
     * @return response object
     * @throws Exception
     */
    public HttpResponse<String> sendPostRequest(String url, String json) throws Exception {
        HttpClient client = createHttpClient(); // Create a new HttpClient with SSL context
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url)) // Set the URI for the request
                .header("Authorization", getBasicAuthHeader()) // Add Basic Auth header
                .header("Content-Type", "application/json") // Set the content type to JSON
                .POST(HttpRequest.BodyPublishers.ofString(json)) // Set the request body
                .build();

        // Send the request and return the response
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send GET request to specified url
     *
     * @param url destination of request
     * @return response object
     * @throws Exception
     */
    public HttpResponse<String> sendGetRequest(String url) throws Exception {
        HttpClient client = createHttpClient(); // Create a new HttpClient with SSL context
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url)) // Set the URI for the request
                .header("Authorization", getBasicAuthHeader()) // Add Basic Auth header
                .GET() // Set the request method to GET
                .build();
        // Send the request and return the response
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Create an HttpClient with SSL context that trusts the self-signed certificate.
     *
     * @return HttpClient instance
     * @throws Exception if an error occurs during the creation of the SSL context
     */
    private HttpClient createHttpClient() throws Exception {
        // Load the trust store containing the self-signed certificate
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (var keyStoreStream = getClass().getResourceAsStream("/localhost.p12")) {
            keyStore.load(keyStoreStream, "team2key".toCharArray());
        }

        // Initialize the TrustManager with the trust store
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        // Create SSL context with the trust store
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        // Create HttpClient with the custom SSL context
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }
}
