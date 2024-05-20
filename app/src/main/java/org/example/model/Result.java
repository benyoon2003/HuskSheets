package org.example.model;

import java.util.List;

// Represents a JSON object to be returned by an HTTP request
public class Result {
    // set to true if this Result contains a value
    private boolean success;
    // contains reasoning for failure if this Result has no value
    private String message;
    private List<Argument> value;

    public Result(boolean success, String message, List<Argument> value) {
        this.success = success;
        this.message = message;
        this.value = value;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public List<Argument> getValue() {
        return this.value;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addValue(Argument arg) {
        this.value.add(arg);
    }

}
