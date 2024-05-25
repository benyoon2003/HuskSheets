package org.example.controller;

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


  public void register(){

  }


  public ArrayList<Publisher> getPublishers(){

  }

  public ArrayList<Spreadsheet> getSheets(Publisher publisher){

  }

  public void createSheet(Publisher publisher, Spreadsheet sheet){

  }

  public void deleteSheet(Publisher publisher, Spreadsheet sheet){

  }



//  Result getUpdatesForSubscription(Argument)
//  Result getUpdatesForPublished(Argument)
//  Result updatePublished(Argument)
//  Result updateSubscription(Argument)

}
