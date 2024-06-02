package org.example;

import org.example.model.Spreadsheet;

import java.util.List;

public class CustomResponse {
    private boolean success;
    private String message;
    private List<Spreadsheet> value;
    private long time;

    public CustomResponse(boolean success, String message, List<Spreadsheet> value, long time) {
        this.success = success;
        this.message = message;
        this.value = value;
        this.time = time;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Spreadsheet> getValue() {
        return value;
    }

    public void setValue(List<Spreadsheet> value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
