package org.example.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the result of an operation, encapsulating success status, message,
 * and value.
 */
public class Result {
    private final boolean success;
    private final String message;
    private final List<Argument> value;

    /**
     * Constructs a Result with the specified success status, message, and value.
     *
     * @param success whether the operation was successful.
     * @param message the message associated with the result.
     * @param value   the value associated with the result.
     * @author Ben
     */
    public Result(boolean success, String message, List<Argument> value) {
        this.success = success; // Initialize the success field with the provided value
        this.message = message; // Initialize the message field with the provided value
        this.value = value != null ? value : new ArrayList<>(); // Initialize the value field with the provided list or an empty list if null
    }

    /**
     * Constructs a Result from a JSON string.
     *
     * @param jsonString the JSON string representing the result.
     * @throws JSONException if there is an error parsing the JSON string.
     * @author Ben
     */
    public Result(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString); // Parse the JSON string to create a JSONObject
        this.success = jsonObject.getBoolean("success"); // Extract the success field from the JSON object
        this.message = jsonObject.optString("message", null); // Extract the message field from the JSON object, default to null if not present
        this.value = new ArrayList<>(); // Initialize the value field as an empty list
        JSONArray jsonArray = jsonObject.optJSONArray("value"); // Extract the value array from the JSON object
        if (jsonArray != null) { // If the value array is not null, iterate through it
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entry = jsonArray.optJSONObject(i); // Get each entry in the array as a JSONObject
                if (entry != null) { // If the entry is not null, create an Argument object and add it to the value list
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
     * @author Ben
     */
    public boolean getSuccess() {
        return this.success; // Return the success status
    }

    /**
     * Gets the message associated with the result.
     *
     * @return the message associated with the result.
     * @author Tony
     */
    public String getMessage() {
        return this.message; // Return the message associated with the result
    }

    /**
     * Gets the value associated with the result.
     *
     * @return a list of Argument objects representing the value.
     * @author Tony
     */
    public List<Argument> getValue() {
        return this.value; // Return the list of Argument objects
    }

    /**
     * Converts the Result object to a string representation.
     *
     * @return a string representation of the Result object.
     * @author Tony
     */
    @Override
    public String toString() {
        // Convert the value list to a string
        String valueString = value.stream()
                .map(Argument::toString)
                .collect(Collectors.joining(", ", "[", "]"));

        // Return the string representation of the Result object
        return "Result{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", value=" + valueString +
                '}';
    }
}
