package org.example.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the result of an operation, encapsulating success status, message, and value.
 */
public class Result {

    private boolean success;
    private String message;
    private List<Argument> value;

    /**
     * Constructs a Result with the specified success status, message, and value.
     *
     * @param success whether the operation was successful.
     * @param message the message associated with the result.
     * @param value   the value associated with the result.
     */
    public Result(boolean success, String message, List<Argument> value) {
        this.success = success;
        this.message = message;
        this.value = value != null ? value : new ArrayList<>();
    }

    /**
     * Constructs a Result from a JSON string.
     *
     * @param jsonString the JSON string representing the result.
     * @throws JSONException if there is an error parsing the JSON string.
     */
    public Result(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.success = jsonObject.getBoolean("success");
        this.message = jsonObject.optString("message", null);
        this.value = new ArrayList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("value");

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

    /**
     * Gets the success status of the result.
     *
     * @return true if the operation was successful, false otherwise.
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Gets the message associated with the result.
     *
     * @return the message associated with the result.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the value associated with the result.
     *
     * @return a list of Argument objects representing the value.
     */
    public List<Argument> getValue() {
        return this.value;
    }

    /**
     * Converts the Result object to a string representation.
     *
     * @return a string representation of the Result object.
     */
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
     * Extracts sheet names from a JSON response.
     *
     * @param response the JSON response containing sheet information.
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
     * Extracts the payload of a specified sheet from a JSON response.
     *
     * @param response  the JSON response containing sheet information.
     * @param sheetName the name of the sheet for which the payload is to be extracted.
     * @return the payload of the specified sheet, or null if not found.
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

    /**
     * Creates a response string for a specified endpoint.
     *
     * @param success  whether the operation was successful.
     * @param message  the message associated with the response.
     * @param value    the value associated with the response.
     * @param endpoint the endpoint for which the response is created.
     * @return a response string.
     */
    public static String makeResponse(Boolean success, String message, List<Argument> value, String endpoint) {
        String res = "";

        switch (endpoint) {
            case "register": break;
            case "getPublishers": break;
            case "getSheets": break;
            case "createSheet": break;
            case "deleteSheet": break;
            case "getUpdatesForSubscription": break;
            case "getUpdatesForPublished": break;
            case "updatePublished": break;
            case "updateSubscription": break;
            default: break;
        }

        return res;
    }
    
    /**
     * Main method for testing the Result class.
     *
     * @param args command line arguments.
     * @throws Exception if an error occurs during testing.
     */
    public static void main(String[] args) throws Exception {
        // Example usage
        // getPayload(ServerEndpoint.getUpdatesForSubscription("team2", "testPayload2", "0"), "testPayload2");
    }
}
