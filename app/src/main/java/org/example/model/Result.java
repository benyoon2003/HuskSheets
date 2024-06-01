package org.example.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a utility class to handle JSON responses from HTTP requests.
 */
public class Result {

    /**
     * Parses the JSON response to retrieve a list of sheet names.
     *
     * @param response the JSON response from the server.
     * @return a list of sheet names.
     */
    public static List<String> getSheets(String response) {
        List<String> sheetNames = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entry = jsonArray.getJSONObject(i);
                String sheetName = entry.getString("sheet");
                sheetNames.add(sheetName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sheetNames;
    }

    /**
     * Retrieves the payload of a specified sheet from the JSON response.
     *
     * @param response  the JSON response from the server.
     * @param sheetName the name of the sheet.
     * @return the payload of the specified sheet.
     */
    public static String getPayload(String response, String sheetName) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entry = jsonArray.getJSONObject(i);
                String sheet = entry.getString("sheet");
                if (sheet.equals(sheetName)) {
                    return entry.getString("payload");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // Example usage
        // getPayload(ServerEndpoint.getUpdatesForSubscription("team2", "testPayload2", "0"), "testPayload2");
    }
}
