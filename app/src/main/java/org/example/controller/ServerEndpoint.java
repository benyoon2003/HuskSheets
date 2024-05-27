package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.example.model.IAppUser;
import org.example.model.Publisher;
import org.example.model.Spreadsheet;
import org.h2.util.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Table;



public class ServerEndpoint {

  private String serv_url = "https://husksheets.fly.dev/api/v1/";

  public ServerEndpoint(){

  }


  public ArrayList<Publisher> getPublishers(){
      try {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(serv_url + "/getPublishers"))
            .header("Content-Type", "application/json")
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
          return null;
        } else {
          ObjectMapper mapper = new ObjectMapper();
          return mapper.readValue(response.body(), new TypeReference<ArrayList<Publisher>>() {});
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
  }

  public ArrayList<Spreadsheet> getSheets(Publisher publisher){
    try {
      HttpClient client = HttpClient.newHttpClient();
      String json = String.format("{\"username\": \"%s\"}", publisher.getUsername());
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(serv_url + "/getSheets"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
          .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 401) {
        return null;
      } else {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.body(), new TypeReference<ArrayList<Spreadsheet>>() {});
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void createSheet(Publisher publisher, Spreadsheet sheet){
    try {
      HttpClient client = HttpClient.newHttpClient();
      String json = String.format("{\"username\": \"%s\", \"sheet\": \"%s\"}", publisher.getUsername(), sheet.getName());
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(serv_url + "/createSheet"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
          .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void deleteSheet(Publisher publisher, Spreadsheet sheet){
    try {
      HttpClient client = HttpClient.newHttpClient();
      String json = String.format("{\"username\": \"%s\", \"sheet\": \"%s\"}", publisher.getUsername(), sheet.getName());
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(serv_url + "/deleteSheet"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
          .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



//  Result getUpdatesForSubscription(Argument)
//  Result getUpdatesForPublished(Argument)
//  Result updatePublished(Argument)
//  Result updateSubscription(Argument)

}
