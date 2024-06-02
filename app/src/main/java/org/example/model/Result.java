package org.example.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a utility class to handle JSON responses from HTTP requests.
 */
public class Result {
    private boolean success;
    private String message;
    private List<Argument> value;

    // Existing constructor
    public Result(boolean success, String message, List<Argument> value) {
        this.success = success;
        this.message = message;
        this.value = value != null ? value : new ArrayList<>();
    }

    // New constructor to parse JSON string
    public Result(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.success = jsonObject.getBoolean("success");
        this.message = jsonObject.optString("message", null);
        this.value = new ArrayList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("argument");

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entry = jsonArray.optJSONObject(i);
                if (entry != null) {
                    Argument argument = new Argument();
                    argument.setPublisher(entry.optString("publisher", ""));
                    argument.setSheet(entry.optString("sheet", ""));
                    argument.setId(entry.optString("id", ""));
                    argument.setPayload(entry.optString("payload", ""));
                    this.value.add(argument);
                }
            }
        }
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public List<Argument> getArgument() {
        return this.value;
    }

    @Override
    public String toString() {
        String valueString = value.stream()
                .map(Argument::toString)
                .collect(Collectors.joining(", ", "[", "]"));

        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", value=" + valueString +
                '}';
    }

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


    public static String makeResponse(String response, IAppUser user, String endpoint){
        String res = "";

        switch (endpoint) {


        }

        return res;
    }
    public static void main(String[] args) throws Exception {
        // Example usage
        // getPayload(ServerEndpoint.getUpdatesForSubscription("team2", "testPayload2", "0"), "testPayload2");
    }

}
