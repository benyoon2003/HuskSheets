package org.example.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Represents a JSON object to be returned by an HTTP request
public class Result {

    //Get list of sheets of a specified user
    public static List<String> getSheets(String response){
        List<String> sheetNames = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(response);
        for( String key : jsonObject.keySet()){
            System.out.println(key);
        }

        JSONArray jsonArray = jsonObject.getJSONArray("value");


        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject entry = jsonArray.getJSONObject(i);
            String sheetName = entry.getString("sheet");
            System.out.println(sheetName);
            sheetNames.add(sheetName);
        }
        return sheetNames;
    }

    //Get payload of a specified sheet
    public static String getPayload(String response, String sheetName){
        JSONObject jsonObject = new JSONObject(response);
        for( String key : jsonObject.keySet()){
            System.out.println(key);
        }

        JSONArray jsonArray = jsonObject.getJSONArray("value");
        System.out.println(jsonArray.toString());

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject entry = jsonArray.getJSONObject(i);
            String sheet = entry.getString("sheet");
            if(sheet.equals(sheetName)){
                try {
                    System.out.println(sheet);
                    String payload = entry.getString("payload");
                    System.out.println(payload.toString());
                    return payload;
                } catch (JSONException e) {
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        //getSheets(ServerEndpoint.getSheets("team2"));

        getPayload(ServerEndpoint.getSheets("team2"), "exampleSheet");

    }
}
