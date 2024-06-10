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
    private boolean success;
    private String message;
    private List<Argument> value;

    /**
     * Constructs a Result with the specified success status, message, and value.
     *
     * @param success whether the operation was successful.
     * @param message the message associated with the result.
     * @param value   the value associated with the result.
     * @author Ben
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
     * @author Ben
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
     * @author Ben
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Gets the message associated with the result.
     *
     * @return the message associated with the result.
     * @author Tony
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the value associated with the result.
     *
     * @return a list of Argument objects representing the value.
     * @author Tony
     */
    public List<Argument> getValue() {
        return this.value;
    }

    /**
     * Converts the Result object to a string representation.
     *
     * @return a string representation of the Result object.
     * @author Tony
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
}
